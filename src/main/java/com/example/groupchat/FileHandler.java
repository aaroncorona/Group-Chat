package com.example.groupchat;

import java.io.*;
import java.net.*;
import java.awt.*;

public class FileHandler implements Runnable {

    // Make public
    Socket socket; // local socket reference for the constructor
    Socket[] clientSocketArray;
    BufferedReader input;
    PrintWriter output;
    String message;

    // Constructor
    public FileHandler (Socket socketParam) {
        // Pass in Client Socket (Server side Socket)
        this.socket = socketParam; // this updates the local variable to equal the param
        System.out.println("Client file reader started!");
    }

    // Create run method for the Thread to read
    // This is required for the Class to be passed into a Thread object
    @ Override
    public void run () {
        try {
            // Create Input Stream
            DataInputStream fileIn = new DataInputStream(socket.getInputStream());
            // Read file name size (this is the first thing the Client sends)
            int fileNameLength = fileIn.readInt();
            System.out.println("fileNameLength received = " + fileNameLength);
            // Read file name
            byte[] fileNameByteArray = new byte[fileNameLength]; // Create Name Byte shell
            fileIn.readFully(fileNameByteArray, 0, fileNameLength); // Fill byte array
            String fileName = new String(fileNameByteArray); // Convert byte name file to String
            // Read file content
            int fileLength = fileIn.readInt(); // First get the length (this is the first thing the Client sends)
            byte[] fileByteArray = new byte[fileLength]; // Create Name Byte shell
            fileIn.readFully(fileByteArray, 0, fileLength); // Fill byte array
            // Confirm byte array was filled correctly
            // Convert byte to file
            String pathToNewFileDir = "/Users/aaroncorona/Desktop/"; // Send to the desktop by default
            String pathToNewFile = "/Users/aaroncorona/Desktop/" + fileName; // Send to the desktop by default
            FileOutputStream fos = new FileOutputStream(pathToNewFile); // use file name from client
            fos.write(fileByteArray); // Convert bytes to file name specified above
            // Now select that file and confirm it looks right
            File fileReceived = new File(pathToNewFile);
            if(fileReceived.exists() == true) {
                System.out.println("File received from client!");
                Desktop desktop = Desktop.getDesktop();
                desktop.open(fileReceived);
                System.out.println("File opened!");
            }
            fileIn.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}