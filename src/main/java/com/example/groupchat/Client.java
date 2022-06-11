package com.example.groupchat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    // This class runs the Main method, which means the class is static by default, it does not create objects
    // There are no other objects created here from another class either, so everything here can be static
    public static BufferedReader inputServer;
    public static Boolean clientLeftChat = false;
    public static int option;
    public static File fileSelected;
    public static String fileNameSelected;
    public static Socket socketForFiles;
    public static Socket socketForChat;
    public static Thread readingThread;
    public static Scanner scan;
    public static String message = "Default";  // set dummy initial value to start the loop
    public static String fullMessage = "Default";  // set dummy initial value to start the loop
    public static String serverMessage = "Default";  // set dummy initial value to start the loop
    public static String username = "Default";
    public static final String escapeMesssage = "EXIT";
    public static PrintWriter writer;
    public static DefaultListModel allMessages = new DefaultListModel();
    // Not static so each client gets their own GUI
    public static JPanel panel;
    public static JFrame frame;
    public static JList list;

    public static void main(String[] args) {
        try {
            // Create Socket 1 (Client side for bytes)
            Socket socketForFiles = new Socket("192.168.1.130", 1111); // Address for the Server
            // Create Socket 2 (Client side for strings)
            Socket socketForChat = new Socket("192.168.1.130", 1111); // Address for the Server
            // Write messages to Server (no Thread needed because this is not a listener)
            // Create Writer to Server (this works better for text than the byte output stream)
            PrintWriter writer = new PrintWriter(socketForChat.getOutputStream(), true);

            // Setup username
            scan = new Scanner(System.in);
            System.out.println("Please enter your username for the groupchat:");
            String username = scan.nextLine();

            // Check if the user wants to send a file
            System.out.println("Do you want to set a profile picture? If so, please type yes to select a file.");
            //            if(scan.nextLine().toLowerCase().contains("yes")) {
//                // Send File
//                // Create DataOutputStream
//                DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socketForFiles.getOutputStream()));
//                // Let the user select a file
//                // Create the chooser object
//                JFileChooser chooser = new JFileChooser("/Users/aaroncorona/Desktop");
//                chooser.setDialogTitle("FILE SELECTOR");
//                // Open file screen
//                option = chooser.showOpenDialog(null);
//                // Create File object for choosen file
//                if (option == JFileChooser.APPROVE_OPTION) {
//                    File fileSelected = new File(String.valueOf(chooser.getSelectedFile()));
//                    String fileNameSelected = chooser.getSelectedFile().getName();
//                    System.out.println("You chose this file: " + fileSelected);
//                    System.out.println("You chose this file name: " + fileNameSelected);
//                    // Create File Input Stream to be able to convert the file to Bytes
//                    FileInputStream fin = new FileInputStream(fileSelected);
//                    // Convert the File Name to a byte array so it can be sent over the Output Stream
//                    byte[] fileNameBytesArray = fileNameSelected.getBytes();
//                    // Convert the File Content to a byte array so it can be sent
//                    byte[] fileBytesArray = new byte[(int) fileSelected.length()];
//                    fin.read(fileBytesArray); // Push file bytes into the array
//                    // Write file name (in bytes) to the Server
//                    out.writeInt(fileNameBytesArray.length); // Always write the length first for the Server to know
//                    out.write(fileNameBytesArray);
//                    // Write file content (in bytes) to the Server
//                    out.writeInt(fileBytesArray.length); // Always write the length first for the Server to know
//                    out.write(fileBytesArray);
//                    System.out.println("File sent!");
//                    out.close();
//                }
//            }

            System.out.println("You can now type a message. Type " + escapeMesssage + " to leave the chat");

            // GUI to capture user message and print chat result
            // Frame
            frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 500);
            frame.setTitle("Chat Window for " + username);
            frame.setLocationRelativeTo(null);
            // Panel
            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(Color.cyan);
            frame.add(panel);
            frame.setVisible(true);
            // Jlist with all the messages
            JList list = new JList(allMessages);
            list.setBackground(Color.CYAN);
            panel.add(list);
            // JTextField to get input
            JTextField textField = new JTextField(10);
            textField.setMaximumSize(new Dimension(300, 30));
            textField.setFocusable(true);
            panel.add(textField, BorderLayout.CENTER);
            // JTextField Listener to get the next user message to send
            textField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JTextField textField = (JTextField) e.getSource();
                    String text = textField.getText();
                    message = text;
                    fullMessage = username + ": " + message;
                    writer.println(fullMessage);  // Send that String to the output stream and clear text box
                    textField.setText("");
                    allMessages.addElement(fullMessage); // Add self message to chat thread
                    if(fullMessage.contains(escapeMesssage) || serverMessage == null) { // prevent endless loop of nulls
                        try {
                            socketForChat.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        readingThread.stop();
                    }
                }
            });
            frame.setVisible(true);
            // Create a thread for the Reader to read groupchat results from the Server
            Thread readingThread = new Thread(){
                @Override
                public void run() {
                    try {
                        // Read all resulting messages (print groupchat)
                        while (clientLeftChat == false) {
                            // Create Reader from Server
                            BufferedReader reader = new BufferedReader(new InputStreamReader(socketForChat.getInputStream()));
                            serverMessage = reader.readLine(); // Read next line if it exists
                            System.out.println(serverMessage); // Print group chat results
                            allMessages.addElement(serverMessage); // Add the message to the Array list
                            if (fullMessage.contains(escapeMesssage) || serverMessage == null) { // prevent endless loop of nulls
                                socketForChat.close();
                                break;
                            }
                        }
                    } catch (IOException er) {
                        System.out.println(er);
                    }
                }
            };
            readingThread.start();

        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
