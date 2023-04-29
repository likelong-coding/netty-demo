package com.lkl.nettyInAction.echo;

import com.lkl.nettyInAction.echo.handler.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        // 调用服务器的start()方法
        new EchoServer(8081).start();
    }

    public void start() throws Exception {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        // ❶ 创建Event-LoopGroup
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // ❷ 创建Server-Bootstrap
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class)  // ❸ 指定所使用的NIO传输Channel
                    .localAddress(new InetSocketAddress(port)) // ❹ 使用指定的端口设置套接字地址
                    .childHandler(new ChannelInitializer() {
                        // ❺ 添加一个EchoServer-Handler到子Channel的ChannelPipeline
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            // EchoServerHandler被标注为@Shareable，所以我们可以总是使用同样的实例
                            ch.pipeline().addLast(serverHandler);
                        }
                    });
            ChannelFuture f = b.bind().sync();   // ❻ 异步地绑定服务器；调用sync()方法阻塞等待直到绑定完成
            f.channel().closeFuture().sync();  // ❼ 获取Channel的CloseFuture，并且阻塞当前线程直到它完成
        } finally {
            group.shutdownGracefully().sync();   //  ❽ 关闭EventLoopGroup，释放所有的资源
        }
    }
}
