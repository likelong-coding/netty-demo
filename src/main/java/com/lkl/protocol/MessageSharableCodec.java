package com.lkl.protocol;

import com.lkl.config.Config;
import com.lkl.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

/**
 * 该解析类要配合 LengthFieldBasedFrameDecoder 一起使用，
 * 保证传递到该handler的消息是完整的【即在LengthFieldBasedFrameDecoder之后初始化】
 * @author likelong
 * @date 2023/1/14
 */
@ChannelHandler.Sharable // 标识该handler可以被共享是线程安全的
public class MessageSharableCodec extends MessageToMessageCodec<ByteBuf, Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        // 设置魔数 4个字节
        out.writeBytes(new byte[]{'N', 'Y', 'I', 'M'});
        // 设置版本 1个字节
        out.writeByte(1);
        // 设置序列化方式 0 jdk, 1 json... 1个字节 ordinal() 序号，0：Java 1：Json
        // 读取配置文件
        out.writeByte(Config.getSerializerAlgorithm().ordinal());
        // 设置指令类型 1字节
        out.writeByte(msg.getMessageType());
        // 设置请求序号 4字节
        out.writeInt(msg.getSequenceId());
        // 无意义，为了补齐为16个字节，填充1个字节的数据
        out.writeByte(0xff);
        // 获取内容字节数组 序列化与反序列化 jdk自带的（效率不高）
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ObjectOutputStream oos = new ObjectOutputStream(bos);
//        oos.writeObject(msg);
//        byte[] bytes = bos.toByteArray();
        byte[] bytes = Config.getSerializerAlgorithm().serialize(msg);
        // 设置正文长度 4字节
        out.writeInt(bytes.length);
        // 写入内容
        out.writeBytes(bytes);
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 怎么编码的就怎么解码
        // 获取魔数
        int magicNum = in.readInt();
        // 获取版本号
        byte version = in.readByte();
        // 获得序列化方式
        byte serializerAlgorithm = in.readByte(); // 0 或 1
        // 获得指令类型
        byte messageType = in.readByte();
        // 获得请求序号
        int sequenceId = in.readInt();
        // 移除补齐字节
        in.readByte();
        // 获得正文长度
        int length = in.readInt();
        // 获得正文
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);

        // 字节数组转成对象
//        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
//        Message message = (Message) ois.readObject();

        // 实际实现类
        Class<? extends Message> messageClass = Message.getMessageClass(messageType);
        Message message = Serializer.Algorithm.values()[serializerAlgorithm].deserialize(messageClass, bytes);
        // 将信息放入List中，传递给下一个handler
        out.add(message);
    }
}
