package com.example.groupchat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
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
    public static final String escapeMesssage = "EXIT";
    public static PrintWriter writer;
    public static ArrayList<String> serverMessageArrayList = new ArrayList<>();
    public static JTextField textField;

    public static void main(String[] args) {
        try {
            // Create Socket 1 (Client side for bytes)
            Socket socketForFiles = new Socket("192.168.1.130", 1111); // Address for the Server
            // Create Socket 2 (Client side for strings)
            Socket socketForChat = new Socket("192.168.1.130", 1111); // Address for the Server

            // Check if the user wants to send a file
            System.out.println("Do you want to send a file? If so, please type yes.");
            Scanner scan = new Scanner(System.in);
            if(scan.nextLine().toLowerCase().contains("y")) {
                // Send File
                // Create DataOutputStream
                DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socketForFiles.getOutputStream()));
                // Let the user select a file
                // Create the chooser object
                JFileChooser chooser = new JFileChooser("/Users/aaroncorona/Desktop");
                chooser.setDialogTitle("FILE SELECTOR");
                // Open file screen
                option = chooser.showOpenDialog(null);
                // Create File object for choosen file
                if (option == JFileChooser.APPROVE_OPTION) {
                    File fileSelected = new File(String.valueOf(chooser.getSelectedFile()));
                    String fileNameSelected = chooser.getSelectedFile().getName();
                    System.out.println("You chose this file: " + fileSelected);
                    System.out.println("You chose this file name: " + fileNameSelected);
                    // Create File Input Stream to be able to convert the file to Bytes
                    FileInputStream fin = new FileInputStream(fileSelected);
                    // Convert the File Name to a byte array so it can be sent over the Output Stream
                    byte[] fileNameBytesArray = fileNameSelected.getBytes();
                    // Convert the File Content to a byte array so it can be sent
                    byte[] fileBytesArray = new byte[(int) fileSelected.length()];
                    fin.read(fileBytesArray); // Push file bytes into the array
                    // Write file name (in bytes) to the Server
                    out.writeInt(fileNameBytesArray.length); // Always write the length first for the Server to know
                    out.write(fileNameBytesArray);
                    // Write file content (in bytes) to the Server
                    out.writeInt(fileBytesArray.length); // Always write the length first for the Server to know
                    out.write(fileBytesArray);
                    System.out.println("File sent!");
                    out.close();
                }
            }

            // Setup username
            System.out.println("Please enter your username for the groupchat:");
            String username = scan.nextLine();

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
                            serverMessageArrayList.add(serverMessage); // Add the message to the Array list
                            if (fullMessage.contains(escapeMesssage) || serverMessage == null) { // prevent endless loop of nulls
                                socketForChat.close();
                                break;
                            }
                            // Display the groupchat on a GUI
                            // Frame
                            JFrame frame = new JFrame();
                            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            frame.setSize(600, 600);
                            frame.setTitle("CHAT WINDOW");
                            frame.setLocationRelativeTo(null);
                            // Panel
                            JPanel panel = new JPanel();
                            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                            panel.setBackground(Color.cyan);
                            frame.add(panel);
                            // Jlist with message array list
                            JList list = new JList(serverMessageArrayList.toArray());
                            panel.add(list); //, layout.CENTER);
                            // JTextField
                            JTextField textField = new JTextField(10);
                            panel.add(textField);
                            textField.setMaximumSize(new Dimension(200, 30));
                            textField.setFocusable(true);
                            // JTextField Listener
                            textField.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    JTextField textField2 = (JTextField) e.getSource();
                                    String text = textField2.getText();
                                    message = text;
                                    fullMessage = username + ": " + message;
                                    System.out.println(fullMessage);
                                }
                            });
                            frame.setVisible(true);
                            writer.println(fullMessage);  // Then, send that String to the output stream
                        }
                    } catch (IOException er) {
                        System.out.println(er);
                    }
                }
            };
            readingThread.start();

            // Write messages to Server (no Thread needed because this is not a listener)
            // Create Writer to Server (this works better for text than the byte output stream)
            PrintWriter writer = new PrintWriter(socketForChat.getOutputStream(), true);
            //  Scan for user messages
            System.out.println("You can now type a message. Type " + escapeMesssage + " to leave the chat");
            while (clientLeftChat == false) { // Keep printing the strings typed, exit the loop when the escape message is typed
                // Write individual client messages to the server
                message = scan.nextLine(); // Each loop updates the string value based on the latest Scanner
                fullMessage = username + ": " + message;
                writer.println(fullMessage);  // Then, send that String to the output stream
                if(fullMessage.contains(escapeMesssage) || serverMessage == null) { // prevent endless loop of nulls
                    socketForChat.close();
                    readingThread.stop();
                    break;
                }

            }
        } catch (IOException errrr) {
            System.out.println(errrr);
        }
    }
}
