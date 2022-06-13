package com.example.groupchat;

import java.io.*;
import java.net.*;

public class Server {

    public static void main(String[] args) {
        try {
            // Create Server Socket (end point for other sockets to connect to)
            String host = "192.168.1.130"; // This should be your private IP address
            int port = 1111; // Pick any port on your device
            InetAddress inetAddress = InetAddress.getByName(host);
            ServerSocket serverSocket = new ServerSocket(port, 5, inetAddress);
            System.out.println("Server created. Waiting for at least 1 client to connect...");

            // Create Client Server objects
            // Each Client gets 2 servers (6 sockets total), one is for transmitting file (byte) data, and the other for transmitting Strings for chats (print writer)
            while (!serverSocket.isClosed()) {
                // Continually allow new socket connections (new clients)
                Socket clientFileSocket = serverSocket.accept();
                Socket clientChatSocket = serverSocket.accept();
                System.out.println("Client connected!");
                // Create a thread for Listening to that Clients files (WIP)
                FileHandler file1 = new FileHandler(clientFileSocket);
                Thread client1FileThread = new Thread(file1);
                client1FileThread.start();
                // Create a thread for Listening to that Clients messages
                ChatHandler chat1 = new ChatHandler(clientChatSocket);
                Thread client1ChatThread = new Thread(chat1);
                client1ChatThread.start();
            }
        }

        catch (IOException e) {
            System.out.println(e);
        }
    }
}

// Educational note about Threads:
// In general, multi-threading is needed to process 2 clients at once, otherwise java can only have 1 listener at a time (listener stops the program)
// A thread = executing a sequence of code, which can be independent of other code
// We must use threads on server side so that whenever a client request comes, a separate thread can be assigned for handling each request