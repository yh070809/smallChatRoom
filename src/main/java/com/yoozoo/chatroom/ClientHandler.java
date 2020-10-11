package com.yoozoo.chatroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class ClientHandler implements Runnable{
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
   // private ArrayList<ClientHandler> clients;
    private HashMap<String,ClientHandler> clients;




    public ClientHandler (Socket clientSocket, HashMap<String,ClientHandler> clients) throws IOException {
        this.client = clientSocket;
        this.clients = clients;
        in = new BufferedReader(new InputStreamReader((client.getInputStream())));
        out = new PrintWriter(client.getOutputStream(),true);
    }


    @Override
    public void run() {
        try{
            while (true){
                String request  = in.readLine();
                if(request.startsWith("Send")){
                    processSendMessage(request);
                }else if (request.startsWith("Read")){
                    processReadMessage(request);
                }else if (request.startsWith("Reply")){
                    processReplyMessage(request);
                }else if(request.startsWith("Forward")){
                    processForwardMessage(request);
                }else if (request.startsWith("Login")){
                    processLogin(request);
                }else if (request.startsWith("Register")){
                    processRegister(request);
                }else if(request.startsWith("Broadcast")){
                    int firstSpace = request.indexOf(" ");
                    if( firstSpace != -1){
                        boardCast(request.substring(firstSpace+1));
                    }
                }else{
                    out.println("[Server] --- Please type valid command");
                }
            }
        }catch (IOException e) {
            System.err.println(e.getStackTrace());
        }finally {
            out.close();
            try {
                in.close();
                out.println("[Server msg] " + Cache.clientThread.get(client.getLocalPort()) + " Offline");
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void boardCast(String msg) {
        for(HashMap.Entry<String, ClientHandler> client : clients.entrySet()){
            client.getValue().out.println(msg);
        }
    }

    private void processSendMessage (String request){
        //verify login
        if(Cache.clientThread.containsKey(client.getLocalPort())){
            String senderName = Cache.clientThread.get(client.getLocalPort());
            String str[] = request.split(" ");
            String receiveName = str[1];
            String sendMsg = "";
            for ( int i = 2; i< str.length; ++i){
                sendMsg += str[i] + " ";
            }
            sendMsg(receiveName,senderName,sendMsg);
        }else{
            out.println("[Server] -- Please login first");
        }

    }

    private void sendMsg (String receiveName,String senderName,String sendMsg){
        // receiver exist
        if(Cache.registeredClient.containsKey(receiveName)){
            // user online
            if(Cache.registeredClient.get(receiveName) != null){
                clients.get(receiveName).out.println(sendMsg);
                 int localPort = clients.get(receiveName).client.getLocalPort();
                 //维护最新消息
                 Map<String,String> map = Cache.Threads.get(localPort);
                 map.put(Cache.RECEIVENAME,senderName);
                 map.put(Cache.MSG,sendMsg);

            }else{
                //Save msg to msgQueue first
                //receiver has unread msg
                if(Cache.msgQueue.containsKey(receiveName)){
                    //sender send already
                    if(Cache.msgQueue.get(receiveName).containsKey(senderName)){
                        Queue<String> q = Cache.msgQueue.get(receiveName).get(senderName);
                        q.offer(sendMsg);
                    }else{
                        //sender first time to send
                        Queue<String> q = new LinkedList<>();
                        q.offer(sendMsg);
                        Cache.msgQueue.get(receiveName).put(senderName,q);
                    }

               }else{
                    Queue<String> q = new LinkedList<>();
                    q.offer(sendMsg);
                    HashMap<String,Queue> map = new HashMap<>();
                    map.put(senderName,q);
                    Cache.msgQueue.put(receiveName,map);
//                    ArrayList<String> list = new ArrayList<>();
//                    list.add(sendMsg);
//                    Cache.msgQ.put(receiveName,list);
                }
                out.println("[Server] -- Receiver offline ,You send message to " + receiveName + " == " + (new Date()).toString());
            }
        }else{
            out.println("[Server] -- The user u send msg is not exit");
        }
    }


    private void processReadMessage(String request){
        String senderName = request.split(" ")[1];
        String curName = Cache.clientThread.get(client.getLocalPort());
        if(Cache.msgQueue.containsKey(curName)){
            Map<String,Queue> map = Cache.msgQueue.get(curName);
            Queue<String> q =  map.get(senderName);
            String str = q.poll();
            out.println( senderName + " : " + str);
            matainlatestMsg(senderName,str);
            //out.println("send from :" + senderName);
            listRemainMessage(curName);
        }else{
            out.println("[Server] -- You do not have unread message");
        }
    }

    private  void processReplyMessage (String request)  {
        Map<String,String> map  = Cache.Threads.get(client.getLocalPort());
        String senderName = map.get(Cache.SENDNAME);
        String receiveName = map.get(Cache.RECEIVENAME);
        String str[] = request.split(" ");
        String sendMsg = "";
        for ( int i = 1; i< str.length; ++i){
            sendMsg += str[i] + " ";
        }
       sendMsg(receiveName,senderName,sendMsg);
    }
    private void processForwardMessage(String request){
        Map<String,String> map  = Cache.Threads.get(client.getLocalPort());
        String senderName = map.get(Cache.SENDNAME);
        String sendMsg = map.get(Cache.MSG);
        String str[] = request.split(" ");
        String receiveName = str[1];
        sendMsg(receiveName,senderName,sendMsg);
    }

    private void  listRemainMessage(String curName){
        if(Cache.msgQueue.containsKey(curName)) {
            Map<String, Queue> map = Cache.msgQueue.get(curName);
            int count =0;
            for(Map.Entry<String, Queue> mq : map.entrySet()){
                if(!mq.getValue().isEmpty()){
                    int size = mq.getValue().size();
                    out.println("[Server] -- You have " + size  +"message from " + mq.getKey());
                }else{
                  count ++;
                }
            }
            if(count == map.size()){
                map.remove(curName);
            }
        }else{
            out.println("[Server] -- You do not have unread message");
        }
    }
    private void processLogin(String request){
        String loginName = request.split(" ")[1];
        if (Cache.registeredClient.containsKey(loginName)){
            // value 赋值为port no. if it has value means this person oneline, null-> offline
            Cache.registeredClient.put(loginName,client.getLocalPort());
//            HashMap<String,String> map = new HashMap<>();
//            map.put(Cache.SENDNAME,loginName);
//            Cache.currentThread.put(client.getLocalPort(),map);

            Cache.clientThread.put(client.getLocalPort(),loginName);
            clients.put(loginName,this);
            out.println("[Server] -- " + loginName +" logged in" + " == " + (new Date()).toString());
           // out.println((new Date()).toString());
            listRemainMessage(loginName);
        }else {
            out.println("This user not exit , please register first");
        }
    }

    private void processRegister(String request){
        String registerName = request.split(" ")[1];
        if(Cache.registeredClient.containsKey(registerName)){
            out.println("[Server] -- " + "User "  + registerName +" already exit");
        }else{
            Cache.registeredClient.put(registerName,null);
            Map<String,String> map = new HashMap<>();
            map.put(Cache.SENDNAME,registerName);
            Cache.Threads.put(client.getLocalPort(),map);
            out.println("[Server] -- " + "User "  + registerName +" registered ");
        }
    }

    private String getThreadName (){
        Map<String,String> map = Cache.Threads.get(client.getLocalPort());
        String curName= map.get(Cache.SENDNAME);
        return  curName;
    }

    private void matainlatestMsg(String reveiceName, String msg){
        Map<String,String> map = Cache.Threads.get(client.getLocalPort());
        map.put(Cache.RECEIVENAME,reveiceName);
        map.put(Cache.MSG,msg);
    }

}



























