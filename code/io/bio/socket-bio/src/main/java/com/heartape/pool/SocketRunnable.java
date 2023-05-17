package com.heartape.pool;

import java.net.Socket;

public class SocketRunnable implements Runnable {

    private final Socket socket;

    public SocketRunnable(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        Connect connect = new Connect(socket);
        if (connect.create()) {
            String s;
            while ((s = connect.receiveMessage()) != null) {
                System.out.println(s);
                connect.sendMessage("我是服务端\n");
            }
            connect.close();
        }
    }
}
