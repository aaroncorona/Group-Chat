# Group Chat
<img width = "300" src = "https://techcrunch.com/wp-content/uploads/2018/10/Facebook-Groups-Chat.png">
<img alt="GitHub commit activity" src="https://img.shields.io/github/commit-activity/m/aaroncorona/Group-Chat">
<img alt="GitHub last commit" src="https://img.shields.io/github/last-commit/aaroncorona/Group-Chat">

## 💬 Overview
A private groupchat that 2+ devices can share over WiFi.

## 📖 Table of Contents
* [Tech Stack](#%EF%B8%8F-tech-stack)
* [File Descriptions](#%EF%B8%8F-file-descriptions)
* [Instructions](#-instructions)
* [Next Release](#-next-release---version-20-features)


## ⚙️ Tech Stack
* Java 
* IntelliJ

## 🗂️ File Descriptions
* **Client.java** - Establishes a client socket, which is composed of 2 sockets (1 to send files, 1 to send text). Also, creates a chat GUI.
* **FileHandler.java** - Reads a file over a particular socket. The server runs this as a thread for each client.
* **ChatHandler.java** - Reads text continually over a particular socket until the socket is closed. Also, relays each message back to all the clients. The server runs this as a thread for each client.
* **Server.java** - Establishes a server socket, which is composed of 2 sockets (1 to accept files, 1 to accept text). Continually creates new server sockets (threads/handelers) to accept more and more new clients.


## 💻 Instructions
1. Pick a device to use as a server (e.g. your laptop). Download the 4 files above on that device. 
2. Edit the Server file to include the Internet address and port appropriate for your device. Edit lines 11-12:
```java
String host = "192.168.1.130"; // This should be your private IP address
int port = 1111; // Pick any port on your device
InetAddress inetAddress = InetAddress.getByName(host); 
ServerSocket serverSocket = new ServerSocket(port, 5, inetAddress); 
```

3. Start the server by running the Server file. You should then see a printed confirmation that the Server is running.
```shell
$ javac Server.java
$ java Server
```
```
Server created. Waiting for at least 1 client to connect...
```
4. Next, each device using the group chat should download Client.java. 
5. Mimic step #2 on Client.java to make sure the host and port on the client (lines 32-33) match the server file (lines 11-12).
6. Each client should answer the 2 questions in the terminal. Then, the program will initialilze the chat GUI. 


<img width="800" alt="3) all" src="https://user-images.githubusercontent.com/31792170/173262250-d7edef4d-53f8-49a6-8999-1609aeeae4f0.png">
<img width="800" alt="3) all" src="https://user-images.githubusercontent.com/31792170/173262302-9f1300e4-a7a9-446b-ba7b-d5c4d4a18b46.png">
<img width="500" alt="4) bye" src="https://user-images.githubusercontent.com/31792170/173262317-284fff18-279c-4f84-9604-cf1f49174212.png">


## 🚧 Next Release - Version 2.0 Features
* Rebuild the GUI with Angular
* Rebuild the backend with Spring
* Add images to the GUI for each user (e.g. like Facebook messenger)
* Improve scrolling




