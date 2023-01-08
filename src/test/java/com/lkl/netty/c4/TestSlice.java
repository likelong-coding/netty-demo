package com.lkl.netty.c4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static com.lkl.netty.utils.ByteBufUtil.log;

/**
 * 分散读？
 * @author likelong
 * @date 2023/1/8
 */
public class TestSlice {

    public static void main(String[] args) {
        // 创建ByteBuf
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10);

        // 向buffer中写入数据
        buffer.writeBytes(new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'});
        log(buffer);

        // 在切片过程中，并没有发生数据复制
        ByteBuf s1 = buffer.slice(0, 5);
        log(s1);
        ByteBuf s2 = buffer.slice(5, 5);
        log(s2);

        // 需要让分片的buffer引用计数加一
        // 避免原Buffer释放导致分片buffer无法使用
        s1.retain();
        s2.retain();

        System.out.println("释放原ByteBuf内存");
        // 实际上没有真正释放内存，因为此时引用计数器还未减到0，就不会影响正常使用
        buffer.release();

        // 切片之后，创建的新的ByteBuf 对最大容量做了限制，再写入数据会抛异常 java.lang.IndexOutOfBoundsException
        // s1.writeByte('x');

        // 更改原始buffer中的值
        System.out.println("===========修改原buffer中的值===========");
        buffer.setByte(0,'z');
        log(buffer);

        System.out.println("===========打印slice1===========");
        log(s1);
    }
}
