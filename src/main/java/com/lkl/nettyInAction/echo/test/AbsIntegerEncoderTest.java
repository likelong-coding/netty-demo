package com.lkl.nettyInAction.echo.test;

import com.lkl.nettyInAction.echo.codec.AbsIntegerEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import static org.junit.Assert.*;

public class AbsIntegerEncoderTest {
    @Test
    public void testEncoded() {
        ByteBuf buf = Unpooled.buffer();   //  ❶创建一个ByteBuf，并且写入9 个负整数
        for (int i = 1; i < 10; i++) {
            // int 4个字节
            buf.writeInt(i * -1);
        }

        EmbeddedChannel channel = new EmbeddedChannel( // ❷创建一个EmbeddedChannel，并安装一个要测试的AbsIntegerEncoder
                new AbsIntegerEncoder());
        assertTrue(channel.writeOutbound(buf));  // ❸写入ByteBuf，并断言调用readOutbound()方法将会产生数据
        assertTrue(channel.finish());  // ❹将该Channel标记为已完成状态

        // read bytes
        for (int i = 1; i < 10; i++) {  // ❺读取所产生的消息，并断言它们包含了对应的绝对值
            assertEquals(i, (int) channel.readOutbound());
        }
        assertNull(channel.readOutbound());
    }
}
