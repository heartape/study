package com.heartape.pool;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 关于keepAliveTime，以前的理解一直有误
 * <p>
 *     keepAliveTime – when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating.
 * </p>
 * <p>
 *     Else if fewer than maximumPoolSize threads are running, a new thread will be created to handle the request only if the queue is full.
 * </p>
 * <p>
 *     综上，keepAliveTime是针对非核心线程
 * </p>
 */
public class Server {

    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                12,
                24,
                10,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(24),
                // RejectedExecutionHandler自带实现类有四个，表示拒绝策略
                new ThreadPoolExecutor.DiscardOldestPolicy());
        try(ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(8080)) {
            for (;;) {
                Socket socket = serverSocket.accept();
                threadPoolExecutor.execute(new SocketRunnable(socket));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
