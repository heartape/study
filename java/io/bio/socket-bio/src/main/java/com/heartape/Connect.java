package com.heartape;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;

public class Connect {

    private final Socket socket;
    private BufferedReader bufferedReader;
    private BufferedOutputStream bufferedOutputStream;

    public Connect(String address, Integer port, Integer localPort) throws IOException {
        InetAddress inet4Address = Inet4Address.getByName(address);
        InetAddress localAddress = Inet4Address.getByName("127.0.0.1");
        socket = new Socket(inet4Address, port, localAddress, localPort);
    }

    public Connect(Socket socket) {
        this.socket = socket;
    }

    public boolean create() {
        InputStream inputStream;
        OutputStream outputStream;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            return false;
        }
        bufferedOutputStream = new BufferedOutputStream(outputStream);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        bufferedReader = new BufferedReader(inputStreamReader);
        return true;
    }

    public void close() {
        try {
            bufferedReader.close();
            bufferedOutputStream.close();
        } catch (IOException e) {
            System.out.println("关闭失败");
        }
    }

    public boolean sendMessage(String message) {
        try {
            bufferedOutputStream.write(message.getBytes());
            bufferedOutputStream.flush();
        } catch (IOException e) {
            System.out.println("连接已断开");
            return false;
        }
        return true;
    }

    public String receiveMessage() {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            return null;
        }
    }
}
