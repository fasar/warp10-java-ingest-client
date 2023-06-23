package com.github.fasar.wijc.core;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;

@Slf4j
public class ReadSocketThread extends Thread {
    private final BufferedReader inputReader;

    public ReadSocketThread(BufferedReader inputReader) {
        this.inputReader = inputReader;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
                if (!inputReader.ready()) continue;
                String line = inputReader.readLine();
                log.info("socket: " + line);
            } catch (Exception e) {
                log.error("Error while reading from socket", e);
            }
        }
    }
}

