package com.example.groupchat;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatHandler implements Runnable  {

    // Create an array of the Chat sockets (to send the groupchat messages back to all at once)
    // Make this static so the list can be shared among all the objects to track them all
    public static ArrayList<Socket> clientChatSocketArrayList = new ArrayList<Socket>();
    Socket socket;
    Boolean clientLeftChat = false; // Signal when to shut of the client connection

    // Constructor
    public ChatHandler (Socket socketParam) {
        // Pass in Client Socket (Server side Socket)
        this.socket = socketParam;  // this updates the local variable to equal the param
        System.out.println("Client message reader started!");
        // Add sockets to list of all sockets so they can all receive the groupchat results
        clientChatSocketArrayList.add(socket);
    }

    // Create run method for the Thread to read
    // This is required for the Class to be passed into a Thread object
    @ Override
    public void run () {
        try {
            // Listen for messages here in a separate thread because listening is a blocking operation
            String message = new String();
            while (clientLeftChat == false) { // Keep scanning until exit message is sent
                // Add message to string variable and print it
                Scanner scanner = new Scanner(socket.getInputStream());
                message = scanner.nextLine(); // Each loop updates the string value based on the latest input from the stream
                System.out.println(message);
                // Parse Username from message string
                String[] messageArray = message.split(":");
                String username = messageArray[0];
                // Look for exit messsage
                if(message.contains("EXIT")) {
                    // Parse username for the exit message
                    message = "* " + username + " left the chat *";
                    System.out.println(message);
                    closeClient();
                }
                // Broadcast message to all clients
                for(int i=0; i < clientChatSocketArrayList.size(); i++) {
                    Socket givenSocket = clientChatSocketArrayList.get(i);
                    if(!socket.equals(givenSocket)) { // Do not relay the message back to the same client
                        // Every loop creates a writer to the given socket
                        PrintWriter output = new PrintWriter(clientChatSocketArrayList.get(i).getOutputStream(), true);
                        // Send message to socket
                        output.println(message);
                        // Confirmation print
//                        System.out.println("message (" + message + ") relayed back to client! Relayed from thread that listens to "+ username);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void closeClient() throws IOException {
        try {
            clientLeftChat = true;
            clientChatSocketArrayList.remove(socket); // Remove from array list to prevent IO error from trying to send a message to a closed socket
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

}