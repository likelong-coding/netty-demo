package com.lkl.netty.c3;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * @author likelong
 * @date 2023/1/3
 */
@Slf4j
public class TestNettyFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 线程池
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        // 拿到里面一个EventLoop 实质上就是一个单线程线程池
        EventLoop eventLoop = eventLoopGroup.next();
        // netty 的 Future 继承自 jdk 的 Future，并进行了一些扩展
        Future<Integer> future = eventLoop.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                // 计算执行 1s
                log.debug("执行计算...");
                Thread.sleep(1000);
                return 1024;
            }
        });

        // 同步
   /*     log.debug("等待结果...");
        log.debug("结果是： {}", future.get());*/

        // 异步
        future.addListener(new GenericFutureListener<Future<? super Integer>>() {
            @Override
            public void operationComplete(Future<? super Integer> future) throws Exception {
                log.debug("接收结果：{}", future.getNow());
            }
        });
    }
}
