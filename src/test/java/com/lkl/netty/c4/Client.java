package com.lkl.netty.c4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @author likelong
 * @date 2022/12/18
 */
public class Client {

    public static void main(String[] args) {
        try (SocketChannel sc = SocketChannel.open()) {
            sc.connect(new InetSocketAddress("localhost", 8080));
            // 消息用 '\n' 分割区分
            sc.write(Charset.defaultCharset().encode("hello\n"));
            sc.write(Charset.defaultCharset().encode("0123456789abcdef"));
            sc.write(Charset.defaultCharset().encode("0123456789abcdef6666\n"));
            System.out.println("waiting...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
