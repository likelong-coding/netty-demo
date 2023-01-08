package com.lkl.netty.c4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

import static com.lkl.netty.utils.ByteBufUtil.log;

/**
 * 集中写？
 *
 * @author likelong
 * @date 2023/1/8
 */
public class TestCompositeByteBuf {

    public static void main(String[] args) {
        ByteBuf buffer1 = ByteBufAllocator.DEFAULT.buffer();
        buffer1.writeBytes(new byte[]{'a', 'b', 'c', 'd', 'e'});

        ByteBuf buffer2 = ByteBufAllocator.DEFAULT.buffer();
        buffer2.writeBytes(new byte[]{'f', 'g', 'z'});

        // 集中到一个ByteBuf里
/*        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        // 链式调用 会发生两次数据复制 性能不是特别好
        buffer.writeBytes(buffer1).writeBytes(buffer2);
        log(buffer);*/

        // 优点：避免了内存的复制，缺点：维护起来更加复杂 分别重新计算读写指针 【零拷贝技术的一些体现】
        CompositeByteBuf buffer = ByteBufAllocator.DEFAULT.compositeBuffer();
        buffer.addComponents(true, buffer1, buffer2);
        log(buffer);
        // 还有就是注意release问题，引用计数 retain
    }
}
