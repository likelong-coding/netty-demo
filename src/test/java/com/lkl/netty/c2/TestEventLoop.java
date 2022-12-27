package com.lkl.netty.c2;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author likelong
 * @date 2022/12/27
 */
@Slf4j
public class TestEventLoop {

    public static void main(String[] args) {
        // 1、创建事件循环组
        EventLoopGroup group = new NioEventLoopGroup(2); // 可以处理 io 事件、普通任务、定时任务
//        EventLoopGroup group = new DefaultEventLoopGroup(); // 处理普通任务、定时任务
//        System.out.println(NettyRuntime.availableProcessors());
        // 2、获取下一个事件循环对象 循环获取
        System.out.println(group.next()); // 第一个
        System.out.println(group.next()); // 第二个
        System.out.println(group.next()); // 第一个
        System.out.println(group.next()); // 第二个

        // 3、执行普通任务 异步处理
 /*       group.next().submit(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("ok");
        });*/

        // 4、执行定时任务
        group.next().scheduleAtFixedRate(() -> {
            log.debug("ok");
        }, 0, 1, TimeUnit.SECONDS);

        log.debug("main...");

    }
}
