package com.lkl.netty.c4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.nio.charset.StandardCharsets;

import static com.lkl.netty.utils.ByteBufUtil.log;

/**
 * @author likelong
 * @date 2023/1/6
 */
public class TestByteBuf {

    public static void main(String[] args) {

        // 1、创建 ByteBuf 对象 会自动扩容
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();

        // 可以使用环境变量，设置是否开启池化 -Dio.netty.allocator.type={unpooled|pooled}

        // class io.netty.buffer.UnpooledByteBufAllocator$InstrumentedUnpooledUnsafeHeapByteBuf

        // 默认池化 + 直接内存 buffer()方法 class io.netty.buffer.PooledUnsafeDirectByteBuf
        // 池化 + 堆内存 heapBuffer()方法 class io.netty.buffer.PooledUnsafeHeapByteBuf
        System.out.println(buffer.getClass());

        log(buffer);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            sb.append("a");
        }

        buffer.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8));
        // 默认容量256，会自动扩容
        log(buffer);
    }
}
