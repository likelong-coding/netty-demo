package com.lkl.netty.c4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.nio.charset.Charset;

import static com.lkl.netty.utils.ByteBufUtil.log;

/**
 * @author likelong
 * @date 2023/1/8
 */
public class ByteBufAPITest {

    public static void main(String[] args) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();

        // 向buffer中写入数据 先写 4 个字节
        buffer.writeBytes(new byte[]{1, 2, 3, 4});
        log(buffer);

        // 再写 4 个字节 一个 int 占 4字节
        buffer.writeInt(5);
        log(buffer);

        // 直接写入字符串 一个字母占一个字节
        buffer.writeCharSequence("likelong", Charset.defaultCharset());
        log(buffer);

        /*
        ByteBuf 扩容规则：（默认初始容量为256字节）
        如何写入后数据大小未超过 512 字节，则选择下一个 16 的整数倍进行扩容
            例如写入后大小为 12 字节，则扩容后 capacity 是 16 字节
        如果写入后数据大小超过 512 字节，则选择下一个 2n
            例如写入后大小为 513 字节，则扩容后 capacity 是 2^10=1024 字节（2^9=512 已经不够了）
            扩容不能超过 maxCapacity，否则会抛出java.lang.IndexOutOfBoundsException异常
         */

        // 读取4个字节 每次读取一个字节 读完的部分就被废弃掉了
        System.out.println(buffer.readByte());
        System.out.println(buffer.readByte());
        System.out.println(buffer.readByte());
        System.out.println(buffer.readByte());
        log(buffer);

        // 通过mark与reset实现重复读取
        buffer.markReaderIndex();
        // 读取一个int(4字节)类型数据
        System.out.println(buffer.readInt());
        log(buffer);

        // 恢复到mark标记处 可以重复读取数据
        buffer.resetReaderIndex();
        log(buffer);
    }

}
