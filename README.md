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
* **Client.java** - Establishes a client socket, which is composed of 2 sockets (1 to send files, 1 to send text). Creates a chat GUI for the client.
* **Server.java** - Establishes a server socket, which is composed of 2 sockets (1 to accept files, 1 to accept text). Continually creates new server sockets (threads/handelers) to accept more and more new clients.
* **Main.java** - The JFrame is instantiated
* **snake_high_scores.csv** - The CSV where high scores are stored. The SnakePanel logic reads from here


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




