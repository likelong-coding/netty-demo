package com.lkl.netty.echo;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author likelong
 * @date 2023/1/8
 */
@Slf4j
public class EchoClient {

    public static void main(String[] args) {
        new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override // 使用 channelActive 事件，它会在连接建立后触发
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                super.channelActive(ctx);
                                // 在 handler 中 建议使用 ctx.alloc() 创建 ByteBuf
                                ByteBuf buffer = ctx.alloc().buffer(10);

                                // 首次建立连接，发送 hello 信息
                                buffer.writeBytes("hello".getBytes(StandardCharsets.UTF_8));
                                ctx.writeAndFlush(buffer);

//                                buffer.release();
                            }
                        }).addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) msg;
                                log.debug("{}", byteBuf.toString(Charset.defaultCharset()));
//                                byteBuf.release();
                            }
                        });
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080));
    }
}
