package com.lkl.server.handler;

import com.lkl.message.ChatRequestMessage;
import com.lkl.message.ChatResponseMessage;
import com.lkl.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Mr.Li
 */
@ChannelHandler.Sharable // 必须添加该注解
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        // 获得user所在的channel
        Channel channel = SessionFactory.getSession().getChannel(msg.getTo());
        // 如果双方都在线
        if (channel != null) {
            // 通过接收方与服务器之间的channel发送信息
            channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(), msg.getContent()));
        } else {
            // 通过发送方与服务器之间的channel发送消息
            ctx.writeAndFlush(new ChatResponseMessage(false, "对方用户不存在或离线，发送失败"));
        }
    }
}
