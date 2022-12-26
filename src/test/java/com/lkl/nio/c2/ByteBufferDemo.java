package com.lkl.nio.c2;

import java.nio.ByteBuffer;

import static com.lkl.nio.utils.ByteBufferUtil.debugAll;

/**
 * @author likelong
 * @date 2022/12/18
 */
public class ByteBufferDemo {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        // 模拟粘包+半包
        buffer.put("Hello,world\nI'm Likelong\nHo".getBytes());
        // 调用split函数处理
        split(buffer);
        buffer.put("w are you?\n".getBytes());
        split(buffer);
    }

    private static void split(ByteBuffer buffer) {
        // 切换为读模式
        buffer.flip();
        for (int i = 0; i < buffer.limit(); i++) {
            // 找到一条完整消息
            if (buffer.get(i) == '\n') {
                // 计算每一个消息长度
                int length = i + 1 - buffer.position();
                // 将完整的消息存入新的ByteBuffer
                ByteBuffer target = ByteBuffer.allocate(length);

                // 从 buffer 中读，向 target 中写
                for (int j = 0; j < length; j++) {
                    target.put(buffer.get());
                }
                debugAll(target);
            }
        }

        // 切换为写模式，但是缓冲区可能未读完，这里需要使用compact
        buffer.compact();
    }
}
