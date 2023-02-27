package com.lkl.netty.c1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author likelong
 * @date 2022/12/26
 */
@Slf4j
public class HelloServer {

    public static void main(String[] args) {
        // 1、启动器，负责装配netty组件，启动服务器
        new ServerBootstrap()
                // 2、创建 NioEventLoopGroup，group 组 可以简单理解为 线程池 + Selector   BossEventLoop, WorkerEventLoop(selector, thread)
                .group(new NioEventLoopGroup())
                // 3、选择服务器的 ServerSocketChannel 实现
                .channel(NioServerSocketChannel.class)
                // 池化堆内存
                .childOption(ChannelOption.ALLOCATOR, new UnpooledByteBufAllocator(false))
                // 4、boss 负责处理连接 worker(child) 负责处理读写，决定了worker(child) 能执行哪些操作（handler）
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    // 5、它的作用是待客户端SocketChannel建立连接后，执行initChannel以便添加更多的处理器
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        // 6、添加具体handler
                        nioSocketChannel.pipeline().addLast(new StringDecoder()); // 将 ByteBuf 转换为字符串
                        nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() { // 自定义handler
                            @Override // 读事件
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                // 打印上一步转换好的字符串
                                System.out.println(msg);

                                // 打印分配内存类型
                                log.debug("alloc buf {}", ctx.alloc().buffer());
                            }
                        });
                    }
                })
                // 7、绑定监听端口
                .bind(8080);
    }
}
