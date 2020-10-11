package com.yoozoo.chatroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SeverConnection implements Runnable {
    private Socket server;
    private BufferedReader in;
    private PrintWriter out;


    public SeverConnection(Socket s) throws IOException {
        server = s;
        in = new BufferedReader(new InputStreamReader((server.getInputStream())));
        out = new PrintWriter(server.getOutputStream(),true);
    }

    @Override
    public void run() {
           //Cache.onlineuser.put(user,server.getLocalPort());
           try{
               while (true){
                   String  serverResponse = in.readLine();
                   System.out.println(serverResponse);
                   if(serverResponse == null) break;
               }
           }catch (IOException e){
               e.printStackTrace();
           }finally {
               try {
                   in.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
    }
}
