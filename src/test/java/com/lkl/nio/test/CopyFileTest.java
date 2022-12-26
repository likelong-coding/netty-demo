package com.lkl.nio.test;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author likelong
 * @date 2022/12/26
 */
public class CopyFileTest {

    public static void main(String[] args) {
        try (
                FileChannel from = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
                FileChannel to = FileChannel.open(Paths.get("2.jpg"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        ) {
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
