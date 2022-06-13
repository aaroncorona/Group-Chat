package com.example.groupchat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client extends JPanel {

    // This class runs the Main method, so the class is static by default, it does not create objects
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
    public static String myUsername = "Default";
    public static String myMessage = "Default";  // set dummy initial value to start the loop
    public static boolean userImageUpload = false;
    public static ImageIcon myPictureIcon;
    public static String serverMessage = "Default";  // set dummy initial value to start the loop
    public static final String escapeMesssage = "EXIT";
    public static DefaultListModel allMessages = new DefaultListModel();
//    public static DefaultListModel allUsers = new DefaultListModel();

    public static void main(String[] args) {
        // First, create sockets. Each client needs 1 socket for files and 1 for text
        String host = "192.168.1.130";
        int port = 1111;
        try {
            // Create Socket 1 (Client side for bytes)
            socketForFiles = new Socket(host, port);
            // Create Socket 2 (Client side for strings)
            socketForChat = new Socket(host, port);
            // Write messages to Server (no Thread needed because this is not a listener)
            }
        catch(Exception e) {
             System.out.println(e);
        }

        // Get username
        scan = new Scanner(System.in);
        System.out.println("** INITIAL CLIENT CONFIGURATION **");
        System.out.println("Please enter your username for the groupchat:");
        myUsername = scan.nextLine();

        // Send file to Server
        System.out.println("Do you want to set a picture? If so, please type y to select a file.");
        if (scan.nextLine().toLowerCase().contains("y")) {
            userImageUpload = true;
            sendUserFile();
        }

        // GUI to capture user message and print chat result
        System.out.println("Chat started over port " + port + ". You can now type a message in the window. Type " + escapeMesssage + " to leave the chat");
        generateGUI();

        // Create a thread for the Reader to read groupchat results from the Server
        generateReaderThread();
    }

    // Get user file for their profile pic or use dummy file, then send to server
    public static void sendUserFile(){
        try {
            // Create the chooser object to let the user select a file
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("FILE SELECTOR");
            // Open file screen
            option = chooser.showOpenDialog(null);
            // Create a File object for chosen file
            if (option == JFileChooser.APPROVE_OPTION) {
                fileSelected = new File(String.valueOf(chooser.getSelectedFile()));
                fileNameSelected = chooser.getSelectedFile().getName();
                System.out.println("You chose this file: " + fileSelected);
                // Send if file is image
                if(fileNameSelected.contains(".png")
                   || fileNameSelected.contains(".jpg")
                   || fileNameSelected.contains(".svg")) {
                    // Generate image file for client use
                    ImageIcon myPictureIcon = new ImageIcon(new ImageIcon(fileNameSelected).getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
                    // Next, Send the file to the server
                    // Create File Input Stream to be able to convert the file to Bytes
                    FileInputStream fin = new FileInputStream(fileSelected);
                    // Create DataOutputStream
                    DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socketForFiles.getOutputStream()));
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
                } else{
                    System.out.println("Invalid file. Must be image");
                    userImageUpload = false;
                }
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    // GUI to capture user message and print chat result
    public static void generateGUI(){
        // Frame
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setTitle("Chat Window for " + myUsername);
        frame.setLocationRelativeTo(null);
        // Panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.LIGHT_GRAY);
        frame.add(panel);
        frame.setVisible(true);
        // Jlist with all the messages
        JList messageList = new JList(allMessages);
        messageList.setBackground(Color.CYAN);
        panel.add(messageList);
        // JTextField to get input
        JTextField textField = new JTextField(10);
        textField.setMaximumSize(new Dimension(300, 30));
        textField.setFocusable(true);
        panel.add(textField, BorderLayout.CENTER);
//        // Add image to List if one exists (WIP)
//        if(userImageUpload == true){
//            try {
//                BufferedImage myPicture = ImageIO.read(fileSelected);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        // JTextField Listener to get the next user message to send
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField textField = (JTextField) e.getSource();
                String textInput = textField.getText();
                if(textInput.length()>0) {
                    myMessage = textInput;
                    myMessage = myUsername + ": " + myMessage;
                    // Create Writer to Server (this works better for text than the byte output stream)
                    try {
                        PrintWriter writer = new PrintWriter(socketForChat.getOutputStream(), true);
                        writer.println(myMessage);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    textField.setText("");
                    allMessages.addElement(myMessage); // Add self message to chat thread
                    if (myMessage.contains(escapeMesssage) || serverMessage == null) { // prevent endless loop of nulls
                        myMessage = ("* " + myUsername + " left the chat *");
                        allMessages.addElement(myMessage);
                        try {
                            socketForChat.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        System.exit(0);
                    }
                }
            }
        });
        frame.setVisible(true);
    }

    // Create a thread for the Reader to read groupchat results from the Server
    public static void generateReaderThread() {
        Thread readingThread = new Thread() {
            @Override
            public void run() {
                try {
                    // Read all resulting messages (print groupchat)
                    while (clientLeftChat == false) {
                        // Create Message Reader from Server
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socketForChat.getInputStream()));
                        serverMessage = reader.readLine(); // Read next line if it exists
                        allMessages.addElement(serverMessage); // Add the incoming message to the Array list
                    }
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        };
        readingThread.start();
    }

}
