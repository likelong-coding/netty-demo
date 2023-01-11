package com.lkl.netty.senior;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.StandardCharsets;

/**
 * @author likelong
 * @date 2023/1/11
 */
public class TestLengthFieldBasedFrameDecoder {

    public static void main(String[] args) {
        // 模拟服务器 通过 EmbeddedChannel 对 handler 进行测试
        EmbeddedChannel channel = new EmbeddedChannel(
                // 解析器五个参数详情见：
                // https://nyimac.gitee.io/2021/04/25/Netty%E5%9F%BA%E7%A1%80/#%E9%95%BF%E5%BA%A6%E5%AD%97%E6%AE%B5%E8%A7%A3%E7%A0%81%E5%99%A8
                new LengthFieldBasedFrameDecoder(1024, 0, 4, 1, 5),
                new LoggingHandler(LogLevel.DEBUG)
        );

        // 4个字节的内容长度，实际内容
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        inject(buffer, "hello,world");
        inject(buffer, "hi");

        channel.writeInbound(buffer);
    }

    private static void inject(ByteBuf buf, String msg) {
        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        // 得到数据的长度
        int length = bytes.length;
        // 将数据信息写入buf
        // 写入数据长度标识 4个字节
        buf.writeInt(length);
        // 写入长度标识后的其他信息
        // 假设版本号 1个字节
        buf.writeByte(1);
        // 写入具体的数据
        buf.writeBytes(bytes);
    }
}
