package com.github.fasar.wijc.core;


import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

@Slf4j
public class Warp10WebSocketClient extends WebSocketClient {

    final String writeToken;
    final long reconnectTime = 1000L;

    public Warp10WebSocketClient(URI serverUri, String writeToken) {
        super(serverUri);
        this.writeToken = writeToken;
    }

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        Warp10WebSocketClient client = new Warp10WebSocketClient(new URI("ws://10.0.0.10:8080/api/v0/streamupdate"), "writeTokenCI");
        client.connectBlocking();
        client.send("0// test.class{} 0\n");
        client.send("1// test.class{} 0\n");
        client.send("2// test.class{} 0\n");
        client.send("3// test.class{} 0\n");
        client.send("4// test.class{} 0\n");
        Thread.sleep(1000);
        client.close();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        send("TOKEN " + writeToken + "\n");
        send("ONERROR MESSAGE\n");
        log.debug("Warp10 Websocket - new connection opened");

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.error("Warp10 Websocket closed with exit code " + code + " additional info: " + reason + ". Reconnection in " + reconnectTime + " ms");
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(reconnectTime);
                log.error("Warp10 Websocket try to reconnect");
                this.reconnect();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });
        t.start();
    }

    @Override
    public void onMessage(String message) {
        log.debug("received message: " + message);
    }

    @Override
    public void onMessage(ByteBuffer message) {
        log.debug("received ByteBuffer: " + message);
    }

    @Override
    public void onError(Exception ex) {
        log.error("an error occurred:" + ex);
    }


}