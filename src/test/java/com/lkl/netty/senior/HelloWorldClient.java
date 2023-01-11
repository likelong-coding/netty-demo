package com.lkl.netty.senior;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * @author likelong
 * @date 2023/1/9
 */
@Slf4j
public class HelloWorldClient {

    public static void main(String[] args) {
        // 1、短链接 解决粘包问题 短链接无法解决半包现象
/*        for (int i = 0; i < 10; i++) {
            // 发送十次，建立十次连接断开连接十次
            send();
        }*/

        // 2、定长解码器 消息长度不够要进行填充，浪费内存资源  FixedLengthFrameDecoder
        send();
        log.debug("finished...");

        // 3、行解码器 LineBasedFrameDecoder 解析消息固定分隔符 同样会扫描所有消息寻找换行符，效率不高

        // 4、长度字段解码器 LengthFieldBasedFrameDecoder
    }

    private static void send() {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(worker);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    log.debug("connected...");
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override // 连接 channel 连接成功，会触发该事件
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            log.debug("sending...");
                            // 1、每次发送16个字节的数据，共发送10次 服务端直接将10次发送的数据一次性接收了，发送【粘包】现象（我们本意是发十次一次发16字节）
                     /*       for (int i = 0; i < 10; i++) {
                                ByteBuf buffer = ctx.alloc().buffer();
                                buffer.writeBytes(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15});
                                ctx.writeAndFlush(buffer);
                            }*/

                            // 2、为了解决粘包问题，我们客户端发送一次消息就直接断开连接【短链接】，建立连接与断开连接可以看作是一次完整消息
                            // 就不会发生粘包问题了
                     /*       ByteBuf buffer = ctx.alloc().buffer();
                            buffer.writeBytes(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17});
                            ctx.writeAndFlush(buffer);
                            // 关闭连接
                            ctx.channel().close();*/

                            // 3、约定最大长度为10
                         /*   final int maxLength = 10;
                            // 被发送的数据
                            char c = 'a';
                            ByteBuf buffer = ctx.alloc().buffer(maxLength);
                            // 向服务器发送10个报文
                            for (int i = 0; i < 10; i++) {
                                // 定长byte数组，未使用部分会以0进行填充
                                byte[] bytes = new byte[maxLength];
                                // 生成长度为0~15的数据
                                for (int j = 0; j < (int)(Math.random()*(maxLength-1)); j++) {
                                    bytes[j] = (byte) c;
                                }
                                buffer.writeBytes(bytes);
                                c++;
                            }
                            // 将数据发送给服务器
                            ctx.writeAndFlush(buffer);*/

                            // 3、约定最大长度为 64 约定消息分隔符 '\n'
                            final int maxLength = 64;
                            // 被发送的数据
                            char c = 'a';
                            ByteBuf buffer = ctx.alloc().buffer(maxLength);
                            for (int i = 0; i < 10; i++) {
                                // 生成长度为0~62的数据
                                Random random = new Random();
                                StringBuilder sb = new StringBuilder();
                                for (int j = 0; j < random.nextInt(maxLength - 2); j++) {
                                    sb.append(c);
                                }
                                // 不同消息 以 \n 分隔
                                sb.append("\n");
                                buffer.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8));
                                c++;
                            }
                            // 将数据发送给服务器
                            ctx.writeAndFlush(buffer);
                        }
                    });
                }
            });
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8080).sync();
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            log.error("client error", e);
        } finally {
            worker.shutdownGracefully();
        }
    }
}


