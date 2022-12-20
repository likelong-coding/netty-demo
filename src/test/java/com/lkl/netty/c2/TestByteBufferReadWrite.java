package com.lkl.netty.c2;

import java.nio.ByteBuffer;

import static com.lkl.netty.utils.ByteBufferUtil.debugAll;

/**
 *  参考博客 https://nyimac.gitee.io/2021/04/18/Netty%E5%AD%A6%E4%B9%A0%E4%B9%8BNIO%E5%9F%BA%E7%A1%80/#Netty%E5%AD%A6%E4%B9%A0%E4%B9%8BNIO%E5%9F%BA%E7%A1%80
 * @author likelong
 * @date 2022/12/17
 */
public class TestByteBufferReadWrite {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);

        buffer.put((byte) 0x61);  //'a'
        debugAll(buffer);

        buffer.put(new byte[]{0x62, 0x63, 0x64}); // b c d
        debugAll(buffer);

        //切换为读模式
        buffer.flip();
        System.out.println((char) buffer.get());
        debugAll(buffer);

        //切换至写模式（压缩）
        buffer.compact();
        debugAll(buffer);

        buffer.put(new byte[]{0x65, 0x66, 0x67});
        debugAll(buffer);
    }
}
