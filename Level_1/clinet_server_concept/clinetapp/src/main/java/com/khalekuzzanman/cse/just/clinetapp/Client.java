package com.khalekuzzanman.cse.just.clinetapp;


import java.net.Socket;

public class Client {
    private final String thisMachineIP = "127.0.0.1";
    private final int serverPort;
    private Socket client;

    public Client(int serverPort) {
        this.serverPort = serverPort;
    }

    public void connect() {
        try {
            client = new Socket(
                    thisMachineIP,
            serverPort
            );
        } catch (Exception ignored) {
        }
    }


    private void disconnect() {
        try {
            client.close();
        } catch (Exception ignore) {
        }

    }


}