package com.lkl.protocol;

import com.lkl.message.LoginRequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author likelong
 * @date 2023/1/12
 */
public class TestMessageCodec {

    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
                // 添加帧解码器，避免粘包半包问题
                new LengthFieldBasedFrameDecoder(
                        1024, 12, 4, 0, 0),
                new LoggingHandler(LogLevel.DEBUG),
                new MessageCodec()
        );

        // 先测试一下出站请求 encode
        LoginRequestMessage message = new LoginRequestMessage("root", "247907lkl", "likelong");
        channel.writeOutbound(message);

        // decode
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, message, buffer);

        // 入站
        channel.writeInbound(buffer);
    }
}
