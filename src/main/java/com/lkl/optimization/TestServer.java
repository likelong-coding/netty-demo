package com.lkl.optimization;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author likelong
 * @date 2023/2/19
 */
@Slf4j
public class TestServer {
    public static void main(String[] args) {

        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                // 设置全连接队列，大小为2，超过该数量会报 连接拒绝异常
                //.option(ChannelOption.SO_BACKLOG, 2)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioServerSocketChannel>() {
                    @Override
                    protected void initChannel(NioServerSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = ctx.alloc().buffer();
                                log.info("alloc buf {}", buf);
                            }
                        });
                    }


                })
                .bind(8080);
    }
}
