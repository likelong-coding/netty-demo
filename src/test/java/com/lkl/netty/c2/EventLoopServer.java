package com.lkl.netty.c2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * @author likelong
 * @date 2022/12/27
 */
@Slf4j
public class EventLoopServer {

    public static void main(String[] args) {
        // 多个 EventLoopGroup 与 Channel 有绑定关系，一经绑定，该Channel上的事件都由绑定的一或多个EventLoopGroup 处理
        // 细分2：增加自定义的非NioEventLoopGroup  DefaultEventLoopGroup
        EventLoopGroup group = new DefaultEventLoopGroup();
        // 处理io操作
        new ServerBootstrap()
                // boss + worker
                //  细分1：boss 只负责 NioServerSocketChannel accept 事件    worker 只负责 SocketChannel 上的读写事件
                .group(new NioEventLoopGroup(), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        // 添加两个handler 第一个使用NioEventLoopGroup处理，第二个使用DefaultEventLoopGroup处理
                        ch.pipeline().addLast("handler1", new ChannelInboundHandlerAdapter() {
                            @Override                                          // 没有解码，就是ByteBuf对象
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) msg;
                                log.debug(byteBuf.toString(Charset.defaultCharset()));
                                // 传递给下一个handler处理
                                ctx.fireChannelRead(msg);
                            }
                           // 使用 DefaultEventLoopGroup 处理
                        }).addLast(group, "handler2", new ChannelInboundHandlerAdapter() {
                            @Override                                          // 没有解码，就是ByteBuf对象
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) msg;
                                log.debug(byteBuf.toString(Charset.defaultCharset()));
                            }
                        });
                    }
                }).bind(8080);
    }
}
