package com.lkl.netty.c3;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author likelong
 * @date 2023/1/3
 */
@Slf4j
public class TestJDKFuture {

    /*
    异步没有让单个请求更快的响应，反而由于多线程使响应更慢了，但是异步提高了吞吐量，单位时间内能接收的请求数更多了
     */

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 1、创建线程池
        ExecutorService service = Executors.newFixedThreadPool(2);
        // 获得Future对象 线程间传递结果的一个容器
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                // 计算执行 1s
                log.debug("执行计算...");
                Thread.sleep(1000);
                return 1024;
            }
        });

        // 2、主线程等待结果  get() 阻塞方法，等待执行完成返回结果
        log.debug("等待结果...");
        log.debug("结果是： {}", future.get());

    }
}
