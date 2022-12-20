package com.lkl.netty.c3;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author likelong
 * @date 2022/12/18
 */
public class TestFileChannelTransferTo {
    public static void main(String[] args) {
        try (
                FileChannel from = new FileInputStream("data.txt").getChannel();
                FileChannel to = new FileOutputStream("to.txt").getChannel();
        ) {
            // 效率高，底层会利用操作系统的零拷贝进行优化，但是一次最多传 2g 的数据
            // 如果数据大于 2g 可以循环多次传输，如下：
            long size = from.size();
            long capacity = from.size();
            // 分多次传输
            while (capacity > 0) {
                // transferTo返回值为传输了的字节数
                capacity -= from.transferTo(size - capacity, capacity, to);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
