package com.lkl.server.handler;

import com.lkl.message.RpcRequestMessage;
import com.lkl.message.RpcResponseMessage;
import com.lkl.rpc.ServicesFactory;
import com.lkl.server.service.HelloService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;

@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage rpcMessage) {
        RpcResponseMessage rpcResponseMessage = new RpcResponseMessage();
        try {
            // 设置返回值的属性
            rpcResponseMessage.setSequenceId(rpcMessage.getSequenceId());
            // 返回一个实例
            HelloService service = (HelloService) ServicesFactory.getService(Class.forName(rpcMessage.getInterfaceName()));

            // 通过反射调用方法，并获取返回值
            Method method = service.getClass().getMethod(rpcMessage.getMethodName(), rpcMessage.getParameterTypes());
            // 获得返回值
            Object invoke = method.invoke(service, rpcMessage.getParameterValue());
            // 设置返回值
            rpcResponseMessage.setReturnValue(invoke);
        } catch (Exception e) {
            e.printStackTrace();
            // 设置异常
            rpcResponseMessage.setExceptionValue(new Exception("远程调用出错：" + e.getCause().getMessage()));
        }
        // 向channel中写入Message
        ctx.writeAndFlush(rpcResponseMessage);
    }
}
