package com.heartape;

import java.util.concurrent.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 类似于划定起跑线，一批一批。
 * 可以用于集群下的配置同步操作（等待所有负载同时更新），利用回调更新配置，然后让所有负载读取配置
 */
public class CyclicBarrierCode {

    public static void main(String[] args) {
        simple();
        sync();
    }

    public static void simple() {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(2);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2, () -> {
            Integer i1 = queue.poll();
            Integer i2 = queue.poll();
            System.out.println("merge start: " + i1 + " " + i2);
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("merge end: " + i1 + " " + i2);
        });

        AtomicInteger atomicInteger = new AtomicInteger(1);
        for (int i = 1; i <= 6; i++) {
            new Thread(() -> {
                int index = atomicInteger.getAndAdd(1);
                try {
                    queue.put(index);
                    System.out.println("put " + index);
                    cyclicBarrier.await();
                }
                catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                System.out.println("end " + index);
            }).start();
        }
    }

    public static Config config = null;

    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 3, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    public static CyclicBarrier cyclicBarrier = new CyclicBarrier(3, () -> {
        System.out.println("sync start");
        config = new Config();
        System.out.println("sync end");
    });
    public static void sync() {
        // 模拟多台服务器
        Future<Config> future1 = executor.submit(task());
        Future<Config> future2 = executor.submit(task());
        Future<Config> future3 = executor.submit(task());

        try {
            Config config1 = future1.get();
            Config config2 = future2.get();
            Config config3 = future3.get();
            System.out.println("poll config" + config1);
            System.out.println("poll config" + config2);
            System.out.println("poll config" + config3);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        executor.shutdown();
    }

    public static Callable<Config> task(){
        return () -> {
            try {
                // 等待拉取请求集结完毕
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
            return config;
        };
    }

    static class Config {

    }
}
