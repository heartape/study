package com.heartape.multiple;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8080));
        // socketChannel.configureBlocking(false);
        ByteBuffer byteBuffer = ByteBuffer.allocate(32);
        byteBuffer.put("我是客户端".getBytes(StandardCharsets.UTF_8));
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
        byteBuffer.clear();
        byte[] bytes = new byte[32];
        int length;
        while ((length = socketChannel.read(byteBuffer)) >= 0) {
            if (length == 0) continue;
            byteBuffer.flip();
            byteBuffer.get(bytes, 0, length);
            System.out.println(new String(bytes, 0, length));
            byteBuffer.clear();
        }
    }
}
