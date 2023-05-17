package com.heartape.simple;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class Server {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = new Server();
        server.start();
    }

    public void start() throws IOException, InterruptedException {

        Selector bossSelector = Selector.open();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8080));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(bossSelector, SelectionKey.OP_ACCEPT);

        for (;;) {
            bossSelector.select();
            TimeUnit.SECONDS.sleep(1);
            System.out.println("boss start");

            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            ByteBuffer writeBuffer = ByteBuffer.allocate(1024);

            Iterator<SelectionKey> iterator = bossSelector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectedKey = iterator.next();
                if (selectedKey.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) selectedKey.channel();
                    SocketChannel socketChannel = channel.accept();
                    if (socketChannel == null) {
                        iterator.remove();
                        continue;
                    }
                    socketChannel.configureBlocking(false);
                    socketChannel.register(bossSelector, SelectionKey.OP_READ);
                } else if (selectedKey.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) selectedKey.channel();
                    int length;
                    if ((length = socketChannel.read(readBuffer)) > 0) {
                        readBuffer.flip();
                        byte[] bytes = new byte[length];
                        readBuffer.get(bytes);
                        System.out.println("数据:" + new String(bytes, StandardCharsets.UTF_8));

                        writeBuffer.clear().put("我是服务端".getBytes(StandardCharsets.UTF_8)).flip();
                        socketChannel.write(writeBuffer);
                        System.out.println("响应已发送");
                        socketChannel.register(bossSelector, SelectionKey.OP_READ);
                    }
                    if (length == -1) {
                        selectedKey.cancel();
                        iterator.remove();
                    }
                }
            }
        }
    }
}
