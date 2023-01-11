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

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * @author likelong
 * @date 2023/1/11
 */
public class TestRedisProtocol {

    /*
    协议设计与解析
    见资料：
    https://nyimac.gitee.io/2021/04/25/Netty%E5%9F%BA%E7%A1%80/#2%E3%80%81%E5%8D%8F%E8%AE%AE%E8%AE%BE%E8%AE%A1%E4%B8%8E%E8%A7%A3%E6%9E%90
     */

    public static void main(String[] args) {
        /*
        如果我们要向Redis服务器发送一条set name Likelong的指令，需要遵守如下协议

        // 该指令一共有3部分，每条指令之后都要添加回车与换行符
        *3\r\n
        // 第一个指令的长度是3
        $3\r\n
        // 第一个指令是set指令
        set\r\n
        // 下面的指令以此类推
        $4\r\n
        name\r\n
        $8\r\n
        Likelong\r\n
         */

        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            // 使用 netty客户端 向 redis服务器 发送命令
            ChannelFuture channelFuture = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // 打印日志
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override // 建立好连接就发送命令
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    super.channelActive(ctx);
                                    // 回车与换行符
                                    final byte[] LINE = {'\r', '\n'};
                                    // 获得ByteBuf
                                    ByteBuf buffer = ctx.alloc().buffer();
                                    // 连接建立后，向Redis中发送一条指令，注意添加回车与换行
                                    // set name Likelong
                                    buffer.writeBytes("*3".getBytes());
                                    buffer.writeBytes(LINE);
                                    buffer.writeBytes("$3".getBytes());
                                    buffer.writeBytes(LINE);
                                    buffer.writeBytes("set".getBytes());
                                    buffer.writeBytes(LINE);
                                    buffer.writeBytes("$4".getBytes());
                                    buffer.writeBytes(LINE);
                                    buffer.writeBytes("name".getBytes());
                                    buffer.writeBytes(LINE);
                                    buffer.writeBytes("$8".getBytes());
                                    buffer.writeBytes(LINE);
                                    buffer.writeBytes("Likelong".getBytes());
                                    buffer.writeBytes(LINE);
                                    ctx.writeAndFlush(buffer);
                                }

                                // 接收redis 给我们的响应
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ByteBuf buf = (ByteBuf) msg;
                                    System.out.println(buf.toString(Charset.defaultCharset()));
                                }
                            });
                        }
                    })
                    .connect(new InetSocketAddress("localhost", 6379));
            channelFuture.sync();
            // 关闭channel
            channelFuture.channel().close().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 关闭group
            group.shutdownGracefully();
        }
    }

}
