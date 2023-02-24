package com.lkl.rpc;

import com.lkl.message.RpcRequestMessage;
import com.lkl.protocol.MessageSharableCodec;
import com.lkl.protocol.ProtocolFrameDecoder;
import com.lkl.server.handler.RpcResponseMessageHandler;
import com.lkl.server.service.HelloService;
import com.lkl.utils.SequenceIdGenerator;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

/**
 * 客户端改进
 * https://nyimac.gitee.io/2021/04/25/Netty%E5%9F%BA%E7%A1%80/#%E6%94%B9%E8%BF%9B%E5%AE%A2%E6%88%B7%E7%AB%AF
 */
@Slf4j
public class RPCClientManager {

    private static Channel channel = null;
    private static final Object LOCK = new Object();

    // 单例模式获取channel对象
    public static Channel getChannel() {
        if (channel != null) {
            return channel;
        }

        // 双重检测锁
        synchronized (LOCK) {
            if (channel != null) {
                return channel;
            }
            initChannel();
            return channel;
        }
    }

    private static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageSharableCodec messageSharableCodec = new MessageSharableCodec();

        // PRC 响应消息处理器
        RpcResponseMessageHandler rpcResponseMessageHandler = new RpcResponseMessageHandler();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProtocolFrameDecoder());
                ch.pipeline().addLast(loggingHandler);
                ch.pipeline().addLast(messageSharableCodec);
                ch.pipeline().addLast(rpcResponseMessageHandler);
            }
        });
        try {
            channel = bootstrap.connect(new InetSocketAddress("localhost", 8080)).sync().channel();
            channel.closeFuture().addListener(promise -> {
                // 【异步关闭 group，避免Channel被阻塞】
                group.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        HelloService service = getProxyService(HelloService.class);

        System.out.println(service.sayHello("likelong"));
//        System.out.println(service.sayHello("xinpeng"));
    }

    // 创建代理类
    public static <T> T getProxyService(Class<T> serviceClass) {
        ClassLoader loader = serviceClass.getClassLoader();
        Class<?>[] interfaces = new Class[]{serviceClass};
        Object o = Proxy.newProxyInstance(loader, interfaces, (proxy, method, args) -> {
            // 1、将方法调用转化为 消息对象
            int sequenceId = SequenceIdGenerator.nextId();
            RpcRequestMessage message = new RpcRequestMessage(
                    sequenceId,
                    serviceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args);

            // 2、将消息发送出去
            getChannel().writeAndFlush(message);

            // 3、等待填充结果 用于获取NIO线程中的返回结果，【获取的过程是异步的】
            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            // 将promise对象放入Map中
            RpcResponseMessageHandler.PROMISES.put(sequenceId, promise);
            // 等待结果被放入Promise中 【成功或者失败都不会抛出异常，下面需要判断】
            promise.await();
            if (promise.isSuccess()) {
                // 调用方法成功，返回方法执行结果
                return promise.getNow();
            } else {
                // 调用方法失败，抛出异常
                throw new RuntimeException(promise.cause());
            }
        });

        return (T) o;
    }

}
