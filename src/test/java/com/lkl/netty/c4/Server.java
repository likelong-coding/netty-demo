package com.lkl.netty.c4;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static com.lkl.netty.utils.ByteBufferUtil.debugRead;

/**
 * @author likelong
 * @date 2022/12/18
 */
@Slf4j
public class Server {

    public static void main(String[] args) {
        // 使用 nio 来理解阻塞模式 单线程

        // ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);

        // 1、创建服务器
        try (ServerSocketChannel ssc = ServerSocketChannel.open()) {
            ssc.configureBlocking(false); // 默认true阻塞模式，false非阻塞模式
            // 2、绑定监听端口
            ssc.bind(new InetSocketAddress(8080));

            // 3、存放连接集合
            List<SocketChannel> channels = new ArrayList<>();

            while (true) {
                // 没有连接阻塞，没有发送数据阻塞
                // 4、accept 建立与客户端之间的连接，SocketChannel 用来与客户端进行通信
                SocketChannel sc = ssc.accept(); // 非阻塞，线程还会继续运行
                if (sc != null) {
                    channels.add(sc);
                    log.debug("connected...{}", sc);
                }
                for (SocketChannel channel : channels) {
                    channel.configureBlocking(false);
                    // 5、接收客户端发送的数据
                    int read = channel.read(buffer);// 非阻塞，或读不到数据，返回0
                    if (read > 0){
                        buffer.flip();
                        debugRead(buffer);
                        buffer.clear();
                        log.debug("after read...{}", channel);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
