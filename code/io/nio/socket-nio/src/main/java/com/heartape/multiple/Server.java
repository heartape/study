package com.heartape.multiple;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.locks.LockSupport;

public class Server {

    private Selector bossSelector;
    private Selector workSelector;
    private Thread main;

    public void start() {
        main = Thread.currentThread();
        try {
            bossSelector = Selector.open();
            workSelector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(8080));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(bossSelector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread bossThread = new Thread(this::startBoss);
        bossThread.start();
        System.out.println("boss start!");
        Thread workThread = new Thread(this::startWork);
        workThread.start();
        System.out.println("work start!");
        LockSupport.park();
    }

    public void startBoss() {
        try {
            for (;;) {
                bossSelector.select();
                System.out.println("获取连接");
                Iterator<SelectionKey> iterator = bossSelector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectedKey = iterator.next();
                    iterator.remove();
                    if (selectedKey.isAcceptable()) {
                        ServerSocketChannel channel = (ServerSocketChannel) selectedKey.channel();
                        SocketChannel socketChannel = channel.accept();
                        if (socketChannel == null) {
                            iterator.remove();
                            continue;
                        }
                        socketChannel.configureBlocking(false);
                        socketChannel.register(workSelector, SelectionKey.OP_READ);
                        System.out.println("注册到工作线程");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void startWork() {
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        ByteBuffer writeBuffer = ByteBuffer.allocate(1024);

        for (;;) {
            try {
                // 似乎不支持再次注册到其他Selector，阻塞在了这里
                workSelector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("读取信息");

            Iterator<SelectionKey> iterator = workSelector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectedKey = iterator.next();
                iterator.remove();
                if (selectedKey.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) selectedKey.channel();
                    int length;
                    try {
                        if ((length = socketChannel.read(readBuffer)) > 0) {
                            byte[] bytes = new byte[length];
                            readBuffer.flip();
                            readBuffer.get(bytes);
                            System.out.println("数据:" + new String(bytes, StandardCharsets.UTF_8));

                            writeBuffer.clear().put("我是服务端".getBytes(StandardCharsets.UTF_8)).flip();
                            socketChannel.write(writeBuffer);
                            System.out.println("响应已发送");
                        }
                        if (length == -1) {
                            selectedKey.cancel();
                            iterator.remove();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void stop() {
        LockSupport.unpark(main);
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
