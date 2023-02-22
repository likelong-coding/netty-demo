package com.lkl.server.handler;

import com.lkl.message.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {

    /**
     * Promise 传递线程间的数据
     * 调用多次方法需要多个Promise
     * 需要一个集合管理这些Promise
     * Map key：value：Promise
     */
    public static final Map<Integer, Promise<Object>> PROMISES = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        log.debug("{}", msg);

        // 获取并移除Map中的Promise
        Promise<Object> promise = PROMISES.remove(msg.getSequenceId());

        if (promise != null) {
            Object returnValue = msg.getReturnValue();
            Exception exception = msg.getExceptionValue();
            if (exception != null) {
                // 返回结果中有异常信息
                promise.setFailure(exception);
            } else {
                // 方法正常执行，没有异常
                promise.setSuccess(returnValue);
            }
        }
    }
}
