package com.lkl.optimization;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author Mr.Li
 */
public class TestParam {
    public static void main(String[] args) {
        // 客户端
        // SocketChannel 5s内未建立连接就抛出异常  （Bootstrap#option 给 SocketChannel配置参数）
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new LoggingHandler());
            ChannelFuture future = bootstrap.connect("127.0.0.1", 8080);
            // netty中线程通信是使用 promise
            future.sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }

        // 服务端
        // ServerSocketChannel 5s内未建立连接就抛出异常  （ServerBootstrap#option 给 ServerSocketChannel配置参数）
//        new ServerBootstrap().option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000);
//        // SocketChannel 5s内未建立连接就抛出异常  （ServerBootstrap#childOption 给 SocketChannel配置参数）
//        new ServerBootstrap().childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);

    }
}
