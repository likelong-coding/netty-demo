package com.lkl.nettyInAction.echo.codec;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

public class SslChannelInitializer extends ChannelInitializer<Channel> {
    private final SslContext context;
    private final boolean startTls;

    public SslChannelInitializer(SslContext context,   //  传入要使用的SslContext
                                 boolean startTls) { //  如果设置为true，第一个写入的消息将不会被加密（客户端应该设置为true）
        this.context = context;
        this.startTls = startTls;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        // 对于每个SslHandler 实例，都使用Channel 的ByteBuf-Allocator 从SslContext 获取一个新的SSLEngine
        SSLEngine engine = context.newEngine(ch.alloc());
        ch.pipeline().addFirst("ssl",
                new SslHandler(engine, startTls));   // 将SslHandler 作为第一个ChannelHandler 添加到ChannelPipeline 中
    }
}
