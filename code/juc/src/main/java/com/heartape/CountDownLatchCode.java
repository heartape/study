package com.heartape;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 用门闩来形容很形象。
 * 类似于局部和整体的关系，整体等待局部完成。
 * 同时也可以理解为对任务优先级的区分，重要的任务会优先执行。
 */
public class CountDownLatchCode {

    public static void main(String[] args) {
        CountDownLatchCode countDownLatchCode = new CountDownLatchCode();

        for (int i = 0; i < 3; i++) {
            new Thread(countDownLatchCode::highPriority).start();
        }

        for (int i = 0; i < 5; i++) {
            new Thread(countDownLatchCode::lowPriority).start();
        }

    }

    private CountDownLatch countDownLatch = new CountDownLatch(3);

    @SuppressWarnings("unused")
    public void resetPriority(){
        countDownLatch = new CountDownLatch(3);
    }

    public void highPriority(){
        try {
            System.out.println("high priority");
            TimeUnit.SECONDS.sleep(2);
            countDownLatch.countDown();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void lowPriority(){
        try {
            countDownLatch.await();
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("low priority");
    }

}
