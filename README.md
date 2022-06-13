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
* **FileHandler.java** - Reads a file over a particular socket. The server runs this as a thread.
* **ChatHandler.java** - Reads text over a particular socket and relays that message back to all the clients. The server runs this as a thread.
* **Server.java** - Establishes a server socket, which is composed of 2 sockets (1 to accept files, 1 to accept text). Continually creates new server sockets (threads/handelers) to accept more and more new clients.



## 💻 Instructions
1. Clone this repo locally 
2. 
```
$ javac Main.java
$ java Main
```

## 🚧 Next Release - Version 2.0 Features
* Add images to the GUI for each user 
* Improved scrolling
* Rebuild the Frontend with Angular
* Open to suggestions!




