package com.lkl.netty.c2;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.lkl.netty.utils.ByteBufferUtil.debugAll;

/**
 * ByteBuffer 与 字符串 相互转换
 *
 * @author likelong
 * @date 2022/12/17
 */
public class ByteBufferStringConvert {

    public static void main(String[] args) {

        // 字符串 转 ByteBuffer
        // 1、 ByteBuffer put方法
        ByteBuffer buffer1 = ByteBuffer.allocate(16);
        buffer1.put("hello".getBytes());
        debugAll(buffer1);

        // 2、CharSet 将字符串转成ByteBuffer，同时已经切换为读模式
        ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("hello");
        debugAll(buffer2);

        // 3、ByteBuffer wrap方法 也是已切换到读模式
        ByteBuffer buffer3 = ByteBuffer.wrap("hello".getBytes());
        debugAll(buffer3);

        // ByteBuffer 转 字符串
        // 通过StandardCharsets解码，获得CharBuffer，再通过toString获得字符串
        String str1 = StandardCharsets.UTF_8.decode(buffer2).toString();
        String str2 = StandardCharsets.UTF_8.decode(buffer3).toString();
        System.out.println(str1);
        System.out.println(str2);

        // buffer2、buffer3 已切换为读模式可以直接转换为字符串，
        // buffer1 还未切换为读模式，先切换为读模式再转成字符串
        buffer1.flip();
        String str3 = StandardCharsets.UTF_8.decode(buffer1).toString();
        System.out.println(str3);

    }
}
