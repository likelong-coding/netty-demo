package com.lkl.optimization;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author Mr.Li
 */
public class TestParam {
    public static void main(String[] args) {
        // 客户端
        //（Bootstrap#option 给 SocketChannel配置参数）
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            // SocketChannel 配置时间内未建立连接就抛出异常（单位：毫秒）
            // bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler())
                            .addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    // 向服务器发送消息
                                    ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("hello, world~".getBytes()));
                                }
                            });
                }
            });
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
