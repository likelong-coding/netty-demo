package com.lkl.netty.c2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

/**
 * @author likelong
 * @date 2022/12/26
 */
public class EventLoopClient {

    public static void main(String[] args) throws InterruptedException {
        // 1、启动器
        Channel channel = new Bootstrap()
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
                // 5、连接服务器
                .connect(new InetSocketAddress("localhost", 8080))
                // Netty 中很多方法都是异步的，如 connect
                // 阻塞方法 sync 方法等待 connect 建立连接完毕
                .sync()
                // 获取 channel 对象，它即为通道抽象，可以进行数据读写操作
                .channel();

        // 一旦客户端与服务器建立连接，该客户端就会与服务器端一个EventLoop建立绑定关系，只要是这个客户端发数据一定是绑定关系的EventLoop进行数据处理
        // 这样做主要就是为了保证数据的线程安全性
        System.out.println(channel);
        System.out.println("");

    }
}
