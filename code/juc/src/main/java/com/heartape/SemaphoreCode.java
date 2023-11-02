package com.heartape;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 使用场景很明确，就是限流。
 */
public class SemaphoreCode {


    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(2, true);

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    semaphore.acquire();
                    TimeUnit.SECONDS.sleep(3);
                    System.out.println("finish");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    semaphore.release();
                }
            }).start();
        }
    }
}
