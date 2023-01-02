package com.lkl.netty.c2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * @author likelong
 * @date 2022/12/26
 */
@Slf4j
public class CloseFutureClient {

    public static void main(String[] args) throws InterruptedException {

        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        channel.pipeline()
                                // netty 内部日志打印
                                .addLast(new LoggingHandler(LogLevel.DEBUG))
                                .addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080));
        Channel channel = channelFuture.sync().channel();
        log.debug("{}", channel);
        // 控制台不断输入，将输入内容发送给客户端，直到输入 "q" 关闭
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                String msg = scanner.nextLine();
                if ("q".equals(msg)) {
                    // close 方法也是异步操作， nio线程来真正执行关闭操作，现在想在 close 关闭完成之后执行一些操作
                    channel.close();
                    // input 线程来执行，两个线程的执行先后顺序不可控 所以，这个后序操作不一定是在 close关闭之后执行
//                    log.debug("处理关闭之后的操作~");
                    break;
                }
                channel.writeAndFlush(msg);
            }
        }, "input").start();

        // 获取 CloseFuture 对象 1）同步处理 2）异步处理
        ChannelFuture closeFuture = channel.closeFuture();
/*        log.debug("waiting close...");
        closeFuture.sync(); // 同步处理，等close 完成之后，再往下执行，保证下面操作安全执行
        // 主线程来执行
        log.debug("处理关闭之后的操作~");*/

        closeFuture.addListener((ChannelFutureListener) future -> {
            // 由 nio线程来执行，关闭完成之后执行
            log.debug("处理关闭之后的操作~");
            // 优雅的关闭，先拒绝接收任务，将未完成的任务完成，未发完的数据发完，最后再关闭
            // 会将 NioEventLoopGroup 里的线程逐一停下来，Java程序也就执行完毕也就会停下来
            group.shutdownGracefully();
        });
    }
}
