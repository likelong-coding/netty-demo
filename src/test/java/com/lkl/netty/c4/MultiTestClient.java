package com.lkl.netty.c4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @author likelong
 * @date 2022/12/24
 */
public class MultiTestClient {

    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 8080));

        sc.write(Charset.defaultCharset().encode("0123456789abcdef"));
        System.out.println("wait...");
    }
}
