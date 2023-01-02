package com.lkl.netty.c2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author likelong
 * @date 2022/12/26
 */
@Slf4j
public class EventLoopClient {

    public static void main(String[] args) throws InterruptedException {
        // 1、启动器  带有 Future、Promise 的类型都是和异步方法配合使用的，使用来处理结果
        ChannelFuture channelFuture = new Bootstrap()
                // 2、添加 EventLoop
                .group(new NioEventLoopGroup())
                // 3、选择客户 Socket 实现类，NioSocketChannel 表示基于 NIO 的客户端实现
                .channel(NioSocketChannel.class)
                // 4、ChannelInitializer 处理器（仅执行一次）
                // 它的作用是待客户端SocketChannel建立连接后，执行initChannel以便添加更多的处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        // 消息会经过通道 handler 处理，这里是将 String => ByteBuf 编码发出
                        channel.pipeline().addLast(new StringEncoder());
                    }
                })
                // 5、连接服务器 connect 异步非阻塞 由主线程发起调用，真正执行 connect 是 nio 线程
                .connect(new InetSocketAddress("localhost", 8080));

        // 阻塞方法 sync 方法等待 connect 建立连接完毕 （同步处理结果）
        // 解决方法一： 使用 sync 方法用来阻塞当前线程，直到nio线程连接建立完毕再往下执行
/*        channelFuture.sync();
        // 无阻塞向下执行获取 channel 对象，它即为通道抽象，可以进行数据读写操作
        Channel channel = channelFuture.channel();

        // 一旦客户端与服务器建立连接，该客户端就会与服务器端一个EventLoop建立绑定关系，只要是这个客户端发数据一定是绑定关系的EventLoop进行数据处理
        // 这样做主要就是为了保证数据的线程安全性
        log.debug("{}", channel);
        channel.writeAndFlush("hello, world");*/

        // 解决方法二：与方法一两者选其一， 使用 addListener(回调对象) 方法异步处理结果，由nio线程处理
        channelFuture.addListener(new ChannelFutureListener() {
            @Override // 待 nio 线程 连接建立完毕之后，会执行 operationComplete 方法
            public void operationComplete(ChannelFuture future) throws Exception {
                Channel channel = channelFuture.channel();
                log.debug("{}", channel);
                channel.writeAndFlush("hello, world");
            }
        });

    }
}
