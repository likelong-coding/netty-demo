package com.lkl.netty.c3;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * @author likelong
 * @date 2023/1/3
 */
@Slf4j
public class TestNettyPromise {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 1、准备 EventLoop 对象
        EventLoop eventLoop = new NioEventLoopGroup().next();

        // 2、可以主动创建 promise，结果容器
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);

        new Thread(() -> {
            // 3、任意一个线程执行计算，计算完毕 向 promise 填充结果
            log.debug("开始计算...");
            try {
//                int i = 1 / 0;
                Thread.sleep(1000);
                // 设置成功结果
                promise.setSuccess(555);
            } catch (Exception e) {
                // 设置异常结果
                promise.setFailure(e);
                e.printStackTrace();
            }
        }).start();

        // 4、接收结果的线程
//        log.debug("等待结果...");
//        // get 阻塞同步
//        log.debug("结果是： {}", promise.get());

        // nio 线程 异步处理结果
        promise.addListener(future -> {
            log.debug("等待结果...");
            log.debug("结果是： {}", promise.get());

            // 优雅的关闭
            eventLoop.shutdownGracefully();
        });
    }
}
