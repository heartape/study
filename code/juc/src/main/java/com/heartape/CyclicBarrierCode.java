package com.heartape;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CyclicBarrierCode {

    public static void main(String[] args) {
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

}
