package com.lkl;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * @author likelong
 */
public class TestNioEventLoop {

    public static void main(String[] args) {
        EventLoop eventLoop = new NioEventLoopGroup().next();
        // 使用NioEventLoop执行任务
        eventLoop.execute(() -> System.out.println("hello"));
    }
}
