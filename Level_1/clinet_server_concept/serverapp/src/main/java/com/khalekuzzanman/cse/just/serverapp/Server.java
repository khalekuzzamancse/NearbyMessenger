package com.khalekuzzanman.cse.just.serverapp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    private ServerSocket server;

    private Socket connectedClient;

    public Server(int port) {
        try {
            server = new ServerSocket(port);
        } catch (IOException ignored) {
        }
    }

    public void runServer()  {
        try {
            while (true) {
                System.out.println("Server: Running");
                connectedClient = server.accept();
                if (connectedClient.isConnected()) {
                    System.out.println("Server: Connected");

                }
            }
        } catch (Exception ignored) {

        }
    }


    void disConnectClient() {
        try {
            connectedClient.close();
        } catch (IOException ignored) {}
    }

}
