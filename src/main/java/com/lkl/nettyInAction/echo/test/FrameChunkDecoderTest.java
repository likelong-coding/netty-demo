package com.lkl.nettyInAction.echo.test;

import com.lkl.nettyInAction.echo.decoder.FrameChunkDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.TooLongFrameException;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FrameChunkDecoderTest {

    @Test
    public void testFramesDecoded() {
        ByteBuf buf = Unpooled.buffer();   //  创建一个ByteBuf，并向它写入9 字节
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();

        // 创建一个EmbeddedChannel，并向其安装一个帧大小为3 字节的FixedLengthFrameDecoder
        EmbeddedChannel channel = new EmbeddedChannel(new FrameChunkDecoder(3));

        assertTrue(channel.writeInbound(input.readBytes(2)));  // 向它写入2 字节，并断言它们将会产生一个新帧
        try {
            channel.writeInbound(input.readBytes(4)); // 写入一个4 字节大小的帧，并捕获预期的TooLongFrameException
            Assert.fail(); // 如果上面没有抛出异常，那么就会到达这个断言，并且测试失败
        } catch (TooLongFrameException e) {
            // expected exception
        }
        assertTrue(channel.writeInbound(input.readBytes(3)));  // 写入剩余的2 字节，并断言将会产生一个有效帧
        assertTrue(channel.finish());  // 将该Channel 标记为已完成状态

        // 读取产生的消息，并且验证值
        ByteBuf read = channel.readInbound();
        assertEquals(buf.readSlice(2), read);
        read.release();

        read = channel.readInbound();
        assertEquals(buf.skipBytes(4).readSlice(3), read);
        read.release();
        buf.release();

    }
}
