package com.yoozoo.chatroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 8888;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(SERVER_IP,SERVER_PORT);
        SeverConnection severConn = new SeverConnection(socket);
        Cache.clientThread.put(socket.getLocalPort(),null);
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
        new Thread(severConn).start();
        briefingBanner();
        while(true){
            String command = keyboard.readLine();
            out.println(command);
            if(command.equals("Logout")) break;
        }
        socket.close();
        System.exit(0);
    }

    public static void  briefingBanner(){
        System.out.print("====================  Welcome to Chat Room =======================" +
                '\n' +
                "Type 'Login [username]' to login" +
                "\n" +
                "Type 'Send [username] [message]' to send message to defined user" +
                "\n" +
                "Type 'Read [username]' to read the message" +
                "\n" +
                "Type 'Replay [message] ' to replay " +
                "\n" +
                "Type 'Forward [username] ' to forward the message to defined user"+
                "\n" +
                "Type 'Register [username] ' to register" +
                "\n" +
                "Type 'Logout' to logout" +"\n"
        );
    }

}
