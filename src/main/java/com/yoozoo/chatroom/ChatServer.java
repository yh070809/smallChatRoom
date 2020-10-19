package com.yoozoo.chatroom;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private static final int PORT = 8888;
    //private static ArrayList<ClientHandler> clients = new ArrayList<>();
    private static HashMap<String,ClientHandler> clients = new HashMap<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(3);

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        int count =0;
        while(true){
            System.out.println("[Server] Waiting for clients connection");
            Socket socket = server.accept();
            count +=1;
            System.out.println("[Server] " + count + " user online");
            System.out.println("socket port:" + socket.getPort());
            ClientHandler clientThread = new ClientHandler(socket,clients);
            //clients.add(clientThread);
            pool.execute(clientThread);
        }
    }
}
