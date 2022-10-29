package com.heartape;

import java.io.IOException;

public class Client {
    public static void main(String[] args) throws IOException {
        Connect connect = new Connect("127.0.0.1", 8080, 8081);
        connect.create();
        for (int i = 0; i < 10; i++) {
            connect.sendMessage("我是客户端\n");
            String response = connect.receiveMessage();
            System.out.println(response);
        }
    }
}
