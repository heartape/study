package com.heartape;

import java.io.IOException;

public class Client {
    public static void main(String[] args) throws IOException {
        com.heartape.bio.Connect connect1 = new com.heartape.bio.Connect("127.0.0.1", 8080, 8081);
        connect1.create();
        connect1.sendMessage("我是客户端\n");
        String response = connect1.receiveMessage();
        System.out.println(response);
    }
}
