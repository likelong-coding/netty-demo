package com.lkl.server;

import com.lkl.protocol.MessageSharableCodec;
import com.lkl.protocol.ProtocolFrameDecoder;
import com.lkl.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Mr.Li
 */
@Slf4j
public class ChatServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        // 这两个handler 不记录消息状态，可以被共享 是线程安全的
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageSharableCodec messageSharableCodec = new MessageSharableCodec();
        LoginRequestMessageHandler loginRequestMessageHandler = new LoginRequestMessageHandler();
        ChatRequestMessageHandler chatRequestMessageHandler = new ChatRequestMessageHandler();
        GroupCreateMessageHandler groupCreateMessageHandler = new GroupCreateMessageHandler();
        GroupChatMessageHandler groupChatMessageHandler = new GroupChatMessageHandler();
        QuitHandler quitHandler = new QuitHandler();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                    ch.pipeline().addLast(loggingHandler);
                    ch.pipeline().addLast(messageSharableCodec);
                    /*
                    IdleStateHandler三个参数： （空闲状态处理器）
                    readerIdleTimeSeconds 读空闲经过的秒数
                    writerIdleTimeSeconds 写空闲经过的秒数
                    allIdleTimeSeconds 读和写空闲经过的秒数
                     */

                    // 用于空闲连接的检测，5s内未读到数据，会触发READ_IDLE事件
                    ch.pipeline().addLast(new IdleStateHandler(5, 0, 0));
                    // 添加双向处理器ChannelDuplexHandler，负责处理READER_IDLE事件
                    ch.pipeline().addLast(new ChannelDuplexHandler() {

                        // 触发特定事件 会执行该方法
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            // 获得事件
                            IdleStateEvent event = (IdleStateEvent) evt;
                            if (event.state() == IdleState.READER_IDLE) {
                                log.info("5s内，未读取到数据");
                                // 断开连接
                                 ctx.channel().close();
                            }
                        }
                    });

                    // 只针对 LoginRequestMessage 消息进行处理
                    ch.pipeline().addLast(loginRequestMessageHandler);
                    ch.pipeline().addLast(chatRequestMessageHandler);
                    ch.pipeline().addLast(groupCreateMessageHandler);
                    ch.pipeline().addLast(groupChatMessageHandler);
                    ch.pipeline().addLast(quitHandler);
                }
            });
            Channel channel = bootstrap.bind(8080).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
