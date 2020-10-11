package com.yoozoo.chatroom;


import org.omg.PortableInterceptor.INACTIVE;
import sun.applet.resources.MsgAppletViewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class Cache {
    /**
     * key is the username value is msg queue
     */
    public static Map<String,Map<String,Queue>> msgQueue = new HashMap<>();

    public static Map<String, ArrayList<String>> msgQ = new HashMap<>();
    /**
     * key is the currentClient localport, value is login name
     */
    public static Map<Integer,String> clientThread = new HashMap<>();
    /**
     * key is the register username, value is socket localport;
     * if value is null means the user is offline
     */
    public static Map<String, Integer> registeredClient = new HashMap<>();

    /**
     * key is socket localport, refer to this thread , value is latest msg type
     */
    public static Map<Integer,Map<String,String>> Threads = new HashMap<>();
    public static String SENDNAME = "SENDNAME";
    public static String RECEIVENAME = "RECEIVENAME";
    public static String MSG = "MSG";
}
