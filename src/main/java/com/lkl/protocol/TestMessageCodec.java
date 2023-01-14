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
        // 添加帧解码器，避免粘包半包问题 粘包会把消息截断、半包会等收到完整消息 随即传给下一个handler处理
        // 记录多个消息状态就是线程不安全的，
        // 多线程状态下可能多个线程消息凑成一个完整消息，传给下一个handler这显然是不对的，应该是线程只处理自己线程的消息
        LengthFieldBasedFrameDecoder FRAME_DECODER = new LengthFieldBasedFrameDecoder(
                1024, 12, 4, 0, 0);
        // 不记录消息状态是线程安全的  @Sharable注解标记 可以共享，线程安全
        LoggingHandler LOGGER_DECODER = new LoggingHandler(LogLevel.DEBUG);

        EmbeddedChannel channel = new EmbeddedChannel(
                FRAME_DECODER,
                LOGGER_DECODER
                ,
                new MessageCodec()
        );

        // 先测试一下出站请求 encode
        LoginRequestMessage message = new LoginRequestMessage("root", "247907lkl", "likelong");
        channel.writeOutbound(message);

        // decode
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, message, buffer);

        // 如果不加帧解码器，可能会出现粘包半包问题导致代码错误
        // 切片模拟半包
        ByteBuf buf1 = buffer.slice(0, 100);
        buf1.retain(); // 引用计数 +1
        ByteBuf buf2 = buffer.slice(100, buffer.readableBytes() - 100);

        // 入站 引用计数会 -1
        channel.writeInbound(buf1);
        channel.writeInbound(buf2);

    }
}
