package com.lkl.nettyInAction.echo.codec;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

public class HttpsCodecInitializer extends ChannelInitializer<Channel> {
    private final SslContext context;
    private final boolean isClient;

    public HttpsCodecInitializer(SslContext context, boolean isClient) {
        this.context = context;
        this.isClient = isClient;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        SSLEngine engine = context.newEngine(ch.alloc());
        pipeline.addFirst("ssl", new SslHandler(engine));   //  将SslHandler 添加到ChannelPipeline 中以使用HTTPS

        if (isClient) {
            pipeline.addLast("codec", new HttpClientCodec());   //  如果是客户端，则添加HttpClientCodec
        } else {
            pipeline.addLast("codec", new HttpServerCodec());   //  如果是服务器，则添加HttpServerCodec
        }
    }
}
