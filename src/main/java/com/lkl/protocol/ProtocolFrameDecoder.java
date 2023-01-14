package com.lkl.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 对 LengthFieldBasedFrameDecoder 统一封装，方便使用 【帧解码器，避免粘包半包问题】
 *
 * @author likelong
 * @date 2023/1/14
 */
public class ProtocolFrameDecoder extends LengthFieldBasedFrameDecoder {

    public ProtocolFrameDecoder() {
        this(1024, 12, 4, 0, 0);
    }

    public ProtocolFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
