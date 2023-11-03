package com.heartape;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 使用场景很明确，就是限流。
 */
public class SemaphoreCode {

    public static void main(String[] args) {
        strict();
        tokenBucket();
    }

    public final static Semaphore strictSemaphore = new Semaphore(2, true);

    public static void strict() {
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    strictSemaphore.acquire();
                    TimeUnit.SECONDS.sleep(3);
                    System.out.println("finish");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    strictSemaphore.release();
                }
            }).start();
        }
    }

    public final static Semaphore tokenBucketSemaphore = new Semaphore(2, true);

    public static void tokenBucket() {

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    tokenBucketSemaphore.acquire();
                    System.out.println("finish");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

        new Thread(() -> {
            for (;;) {
                tokenBucketSemaphore.release();
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ignore) {}
            }
        }).start();
    }
}
