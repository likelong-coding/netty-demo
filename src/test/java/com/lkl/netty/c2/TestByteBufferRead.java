package com.lkl.netty.c2;

import java.nio.ByteBuffer;

import static com.lkl.netty.utils.ByteBufferUtil.debugAll;

/**
 * @author likelong
 * @date 2022/12/17
 */
public class TestByteBufferRead {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a', 'b', 'c', 'd'});
        buffer.flip();

        //rewind 从头开始读
/*        buffer.get(new byte[4]);
        debugAll(buffer);
        buffer.rewind();
        System.out.println((char) buffer.get());*/

        // mark & reset
        //mark 做一个标记，记录 position 位置，reset 是将 position 重置到 mark 的位置
/*        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        buffer.mark(); // 加标记，索引为2的位置
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        buffer.reset(); // 将position重置到索引2
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());*/

        // get(i) 读取指定位置数据，不会改变 position 位置
        System.out.println((char) buffer.get(3));
        debugAll(buffer);
    }
}
