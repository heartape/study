package com.heartape;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 类似于局部和整体的关系，整体等待局部完成，用门闩来形容很形象
 */
public class CountDownLatchCode {

    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(3);

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    System.out.println("start");
                    TimeUnit.SECONDS.sleep(3);
                    System.out.println("finish");
                    countDownLatch.countDown();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    countDownLatch.await();
                    System.out.println("await");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

}
