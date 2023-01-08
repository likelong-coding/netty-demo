package com.lkl.netty.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author likelong
 * @date 2023/1/8
 */
@Slf4j
public class EchoServer {

    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast("h1", new ChannelInboundHandlerAdapter() {
                            @Override                                          // 没有解码，就是ByteBuf对象
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) msg;
                                log.debug("{}", byteBuf.toString(Charset.defaultCharset()));

                                // 转成 字符串 直接传给下一个入站处理器
//                                ctx.fireChannelRead(byteBuf.toString(Charset.defaultCharset()));

                                super.channelRead(ctx, byteBuf.toString(Charset.defaultCharset()));
                            }
                        }).addLast("h2", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                // handler中 建议使用 ctx.alloc() 创建 ByteBuf
                                // 而不是  ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(20)
                                ByteBuf response = ctx.alloc().buffer(20);
                                response.writeBytes(((String) msg).getBytes(StandardCharsets.UTF_8));
                                ctx.writeAndFlush(response);

                                // 使用完最后释放
//                                response.release();
                            }
                        })
                        ;
                    }
                }).bind(8080);
    }
}
