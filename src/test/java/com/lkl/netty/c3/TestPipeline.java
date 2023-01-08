package com.lkl.netty.c3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author likelong
 * @date 2023/1/5
 */
@Slf4j
public class TestPipeline {

    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {

                        // 进站处理器 执行顺序从前(head)往后，出站处理器 执行顺序从后(tail)往前
                        //1、通过 channel 获取 pipeline
                        ChannelPipeline pipeline = ch.pipeline();

                        // Inbound 进站处理器 一般是写入操作
                        // 2、添加处理器 head -> h1 -> h2 -> h4 -> h3 -> h5 -> h6 -> tail
                        // 调试方便给每个 handler 起个名字
                        pipeline.addLast("h1", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("1");
                                // 将原始 ByteBuf 转成 String
                                ByteBuf byteBuf = (ByteBuf) msg;
                                String name = byteBuf.toString(Charset.defaultCharset());
                                // 父类该方法内部会调用fireChannelRead
                                // 并将处理数据传递给下一个handler 入站处理器，如果不调用，调用链就断开了
                                super.channelRead(ctx, name);
                            }
                        });
                        pipeline.addLast("h2", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object name) throws Exception {
                                log.debug("2");
                                Student student = Student.builder().name(name.toString()).build();
                                super.channelRead(ctx, student);
                            }
                        });

                        pipeline.addLast("h4", new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("4");
                                super.write(ctx, msg, promise);
                            }
                        });

                        pipeline.addLast("h3", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object student) throws Exception {
                                log.debug("3， 结果是：{}，class：{}", student, student.getClass());
                                // 向 channel 中写数据
                                // ctx.writeAndFlush 从当前处理器往前找 出站处理器
                                ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("Server...".getBytes(StandardCharsets.UTF_8)));

                                // ch.writeAndFlush 从tail往前找 出站处理器，两者都是从后往前
                                ch.writeAndFlush(ctx.alloc().buffer().writeBytes("Server...".getBytes(StandardCharsets.UTF_8)));

                                // 上述这种情况的输出顺序是 1->2->3->4->6->5->4
                            }
                        });

                        // Outbound 出站处理器，一般是写操作 只有出站处理器向 channel 写入数据才会触发 出站处理器
                        pipeline.addLast("h5", new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("5");
                                super.write(ctx, msg, promise);
                            }
                        });

                        pipeline.addLast("h6", new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("6");
                                super.write(ctx, msg, promise);
                            }
                        });
                    }
                }).bind(8080);
    }

    @Data
    @Builder
    static class Student {
        private String name;
    }
}
