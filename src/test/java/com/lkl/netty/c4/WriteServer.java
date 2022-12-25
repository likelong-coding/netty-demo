package com.lkl.netty.c4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * @author likelong
 * @date 2022/12/23
 */
public class WriteServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();

        ssc.bind(new InetSocketAddress(8080));
        ssc.configureBlocking(false);
        Selector selector = Selector.open();

        ssc.register(selector, SelectionKey.OP_ACCEPT);

        while (true){
            selector.select();
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();

                if(key.isAcceptable()){
                    // 只有一个服务器可以直接 accept 连接事件
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    SelectionKey scKey = sc.register(selector, 0, null);
                    scKey.interestOps(SelectionKey.OP_READ);

                    // 1、向客户端发送大量数据
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 300000000; i++) {
                        sb.append("a");
                    }

                    ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());
                    // 通道容量小于Buffer中的数据大小，导致无法一次性将Buffer中的数据全部写入到Channel中，这时便需要分多次写入

                    // 先执行一次Buffer->Channel的写入，如果未写完，就添加一个可写事件
                    int write = sc.write(buffer);
                    System.out.println(write);
                    // 通道中可能无法放入缓冲区中的所有数据
                    if (buffer.hasRemaining()) {
                        // 注册到Selector中，关注可写事件，并将buffer添加到key的附件中
                        // 关注多个事件
                        scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE);
                        scKey.attach(buffer);
                    }
                }else if(key.isWritable()){
                    // 处理写事件
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    SocketChannel sc = (SocketChannel) key.channel();
                    int write = sc.write(buffer);
                    System.out.println(write);

                    // 清理操作
                    if(!buffer.hasRemaining()){
                        // 缓冲区中内容已全部写出
                        key.attach(null); // 清除附件buffer
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE); // 不需要关注写事件
                    }

                }
            }
        }
    }
}
