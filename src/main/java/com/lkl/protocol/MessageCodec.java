package com.lkl.protocol;

import com.lkl.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * 消息编解码器
 * https://nyimac.gitee.io/2021/04/25/Netty%E5%9F%BA%E7%A1%80/#%E8%87%AA%E5%AE%9A%E4%B9%89%E5%8D%8F%E8%AE%AE
 *
 * @author likelong
 * @date 2023/1/12
 */
public class MessageCodec extends ByteToMessageCodec<Message> {

    /**
     * 编码，出站前，将 自定义Message 编码成 ByteBuf
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        // 设置魔数 4个字节
        out.writeBytes(new byte[]{'N', 'Y', 'I', 'M'});
        // 设置版本 1个字节
        out.writeByte(1);
        // 设置序列化方式 0 jdk, 1 json... 1个字节
        out.writeByte(0);
        // 设置指令类型 1字节
        out.writeByte(msg.getMessageType());
        // 设置请求序号 4字节
        out.writeInt(msg.getSequenceId());
        // 无意义，为了补齐为16个字节，填充1个字节的数据
        out.writeByte(0xff);
        // 获取内容字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] bytes = bos.toByteArray();
        // 设置正文长度 4字节
        out.writeInt(bytes.length);
        // 写入内容
        out.writeBytes(bytes);
    }

    /**
     * 解码，入站时，将 ByteBuf 解码成 Message消息
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 怎么编码的就怎么解码
        // 获取魔数
        int magicNum = in.readInt();
        // 获取版本号
        byte version = in.readByte();
        // 获得序列化方式
        byte serializerType = in.readByte();
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
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message message = (Message) ois.readObject();
        // 将信息放入List中，传递给下一个handler
        out.add(message);

        // 打印获得的信息正文
        System.out.println("===========魔数===========");
        // 魔数 为4字节整数可能比较大
        System.out.println(magicNum);
        System.out.println("===========版本号===========");
        System.out.println(version);
        System.out.println("===========序列化方法===========");
        System.out.println(serializerType);
        System.out.println("===========指令类型===========");
        System.out.println(messageType);
        System.out.println("===========请求序号===========");
        System.out.println(sequenceId);
        System.out.println("===========正文长度===========");
        System.out.println(length);
        System.out.println("===========正文===========");
        System.out.println(message);
    }
}
