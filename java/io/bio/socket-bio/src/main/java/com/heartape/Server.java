package com.heartape;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        for (;;) {
            Socket socket = serverSocket.accept();
            System.out.println("连接已建立");
            new Thread(() -> {
                Connect connect = new Connect(socket);
                if (connect.create()) {
                    String s;
                    while ((s = connect.receiveMessage()) != null) {
                        System.out.println(s);
                        connect.sendMessage("我是服务端\n");
                    }
                    connect.close();
                }
            }).start();
        }
    }
}
