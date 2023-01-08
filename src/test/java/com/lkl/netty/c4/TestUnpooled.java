package com.lkl.netty.c4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;

import static com.lkl.netty.utils.ByteBufUtil.log;

/**
 * Unpooled 是一个工具类，类如其名，提供了非池化的ByteBuf创建、组合、复制等操作
 *
 * @author likelong
 * @date 2023/1/8
 */
public class TestUnpooled {

    public static void main(String[] args) {
        ByteBuf buffer1 = ByteBufAllocator.DEFAULT.buffer();
        buffer1.writeBytes(new byte[]{'a', 'b', 'c', 'd', 'e'});

        ByteBuf buffer2 = ByteBufAllocator.DEFAULT.buffer();
        buffer2.writeBytes(new byte[]{'f', 'g', 'z'});


        /*
        【零拷贝】相关的 wrappedBuffer 方法，可以用来包装 ByteBuf
         */

        // 当包装的 ByteBuf 个数超过一个时， 底层使用了 CompositeByteBuf 【集中写】
        ByteBuf buffer = Unpooled.wrappedBuffer(buffer1, buffer2);
        log(buffer);

        // 也可以用来包装普通字节数组，底层也不会有拷贝操作
        ByteBuf byteBuf = Unpooled.wrappedBuffer(new byte[]{1, 2, 3}, new byte[]{3, 4, 5});
        log(byteBuf);
    }
}
