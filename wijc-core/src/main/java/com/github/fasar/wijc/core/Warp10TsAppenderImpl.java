package com.github.fasar.wijc.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
public class Warp10TsAppenderImpl extends TsAppenderAbstract implements Closeable {

    private final String writeToken;
    private final URL url;
    private OutputStream outputStream;
    private Socket socket;
    private BufferedReader inputReader;
    private boolean socketCantOpenAlreadyLogged = false;


    public Warp10TsAppenderImpl(String url, String writeToken, Warp10TSConfiguration configuration, ObjectMapper mapper) throws MalformedURLException {
        super(configuration, mapper);
        this.url = new URL(url);
        this.writeToken = writeToken;

        Thread thread = new Thread(this::connect);
        thread.start();
    }


    private void connect() {
        try {
            if (this.socket != null && this.socket.isConnected())
                return;

            String host = url.getHost();
            int port = url.getPort() == -1 ? 8080 : url.getPort();
            this.socket = new Socket(host, port);
            this.socket.setKeepAlive(true);
            this.outputStream = socket.getOutputStream();

            String headers = "POST " + url.getPath() + " HTTP/1.1\r\n" +
                    "Host: " + url.getHost() + ":" + url.getPort() + "\r\n" +
                    "X-Warp10-Token: " + this.writeToken + "\r\n" +
                    "Transfer-Encoding: chunked" + "\r\n" +
                    "Content-Type: text/plain\r\n" + "\r\n";
            outputStream.write(headers.getBytes());

            this.inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Thread.sleep(10);
            while (inputReader.ready()) {
                String line = inputReader.readLine();
                System.out.println("socket: " + line);
            }

            socketCantOpenAlreadyLogged = false;

            new ReadSocketThread(inputReader).start();

        } catch (IOException | InterruptedException e) {
            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(100);
                    if (!socketCantOpenAlreadyLogged) {
                        log.error("Failed to connect to server: " + e.getMessage());
                        socketCantOpenAlreadyLogged = true;
                    } else {
                        log.trace("Failed to connect to server: " + e.getMessage());
                    }
                    connect();
                } catch (InterruptedException ex) {
                    log.error("Thread interrupted: " + ex.getMessage());
                    Thread.currentThread().interrupt();
                }
            });
            t.start();

        }
    }


    @Override
    void appendObject(String data) throws IOException {
        // Write the size of the data because HTTP connection is chunked
        outputStream.write(Integer.toHexString(data.length()).getBytes(StandardCharsets.UTF_8));
        outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
        // Write the data
        outputStream.write(data.getBytes(StandardCharsets.UTF_8));
        // Write the end of the data chunk
        outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
        // Flush the data
        outputStream.flush();
    }


    @Override
    public void close() throws IOException {
        outputStream.write("0".getBytes(StandardCharsets.UTF_8));
        outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
        // Flush the data
        outputStream.flush();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (inputReader.ready()) {
            String line = inputReader.readLine();
            System.err.println("socket: " + line);
        }
        outputStream.close();
        socket.close();
    }

}
