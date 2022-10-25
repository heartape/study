package com.heartape;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * RejectedExecutionHandler -> new ThreadPoolExecutor.AbortPolicy()拒绝访问策略
 * new ThreadPoolExecutor.AbortPolicy()
 * AbortPolicy(默认)直接抛出RejectedExecutionException异常阻止系统正常运行
 * DiscardOldestPolicy：抛弃队列中等待最久的任务，然后把当前任务加人队列中尝试再次提交当前任务。
 * DiscardPolicy：该策略默默地丢弃无法处理的任务，不予任何处理也不抛出异常。如果允许任务丢失，这是最好的一种策略。
 * CallerRunsPolicy：“调用者运行”一种调节机制，该策略既不会抛弃任务，也不会抛出异常，而是将某些任务回退到调用者，从而降低新任务的流量。
 */
public class MemoryMergeRequest {

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20, 1, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadPoolExecutor.DiscardPolicy());
        MemoryMergeRequest memoryMergeRequest = new MemoryMergeRequest();
        // 先启动消费线程
        memoryMergeRequest.consumer();
        Thread.sleep(2000);

        // 通过线程池模拟并发请求
        Random random = new Random(3);
        // 实现多个线程开始执行任务的最大并行性,模拟并发
        CountDownLatch countDownLatch = new CountDownLatch(20);
        List<Future<Response>> futureList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Future<Response> submit = threadPoolExecutor.submit(() -> {
                // CountDownLatch.countDown()是用来线程计数(倒数)，等待其他的线程都执行完任务,计数归0主线程就会被唤醒
                countDownLatch.countDown();
                long uid = random.nextLong();
                int count = random.nextInt(5) + 1;
                Request request = new Request(uid, count);
                System.out.println("发起请求:" + request);
                return memoryMergeRequest.producer(request);
            });
            futureList.add(submit);
        }
        futureList.forEach(responseFuture -> {
            try {
                Response response = responseFuture.get(300, TimeUnit.MILLISECONDS);
                System.out.println("返回结果" + response);
            } catch (ExecutionException | TimeoutException | InterruptedException e) {
                e.printStackTrace();
            }
        });

    }
    // 库存量
    private int stock = 30;
    // 通信队列
    private final BlockingQueue<PromiseExchange> queue = new LinkedBlockingQueue<>(10);

    /**
     * 将请求放入队列
     */
    public Response producer(Request request) {
        PromiseExchange promiseExchange = new PromiseExchange(request);
        boolean offer;
        try {
            offer = queue.offer(promiseExchange, 20, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return Response.failure("系统错误");
        }
        if (!offer) {
            return Response.failure("系统繁忙");
        }
        synchronized (promiseExchange) {
            try {
                promiseExchange.wait(200);
            } catch (InterruptedException e) {
                return Response.failure("系统错误");
            }
        }
        return promiseExchange.getResponse();
    }

    public void consumer() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20, 1, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadPoolExecutor.DiscardPolicy());
        threadPoolExecutor.submit(() -> {
            List<PromiseExchange> list = new ArrayList<>();
            while (true) {
                if (queue.isEmpty()) {
                    Thread.sleep(10);
                    continue;
                }
                while (queue.peek() != null) {
                    list.add(queue.poll());
                }

                int sum = list.stream().mapToInt(promiseExchange -> promiseExchange.getRequest().getCount()).sum();
                if (sum <= stock) {
                    stock -= sum;
                    // 生成订单
                    process(list);
                    list.clear();
                    continue;
                }
                // 库存不够就将不能完成的请求返回失败
                System.out.println("不能一次性完成的请求" + list);
                List<PromiseExchange> promiseExchanges = list.stream().filter(promiseExchange -> {
                    Integer count = promiseExchange.getRequest().getCount();
                    if (count > stock) {
                        promiseExchange.setResponse(Response.failure("库存不足:" + promiseExchange));
                        return false;
                    }
                    stock -= count;
                    return true;
                }).collect(Collectors.toList());
                if (promiseExchanges.size() > 0) {
                    System.out.println("筛选后的请求" + promiseExchanges);
                    process(promiseExchanges);
                }
                list.clear();
            }
        });

    }

    /**
     * 订单生成,实际使用时,将多次io转变为一次io
     */
    private void process(List<PromiseExchange> promiseExchangeList) {
        System.out.println("统一处理请求" + promiseExchangeList);
        promiseExchangeList.forEach(promiseExchange -> {
            String orderId = UUID.randomUUID().toString();
            promiseExchange.setResponse(Response.success(orderId));
            System.out.println("生成订单" + promiseExchange);
            // 处理完之后就唤醒对应线程
            synchronized (promiseExchange) {
                promiseExchange.notify();
            }
        });

    }
}

/**
 * 跨线程通信数据封装
 */
@Data
class PromiseExchange {
    private Request request;
    private Response response;

    public PromiseExchange(Request request) {
        this.request = request;
    }
}

/**
 * 请求数据
 */
@Data
class Request {
    private Long uid;
    private Integer count;

    public Request(Long uid, Integer count) {
        this.uid = uid;
        this.count = count;
    }
}

/**
 * 响应数据
 */
@Data
class Response {
    private Boolean success;
    private String message;
    private String orderId;

    public static Response success(String orderId) {
        Response response = new Response();
        response.setSuccess(true);
        response.setMessage("success");
        response.setOrderId(orderId);
        return response;
    }

    public static Response failure(String message) {
        Response response = new Response();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
}

