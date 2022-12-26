package com.lkl.nio.c2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author likelong
 * @date 2022/12/18
 */
public class TestGatheringWrite {
    public static void main(String[] args) {
        ByteBuffer b1 = StandardCharsets.UTF_8.encode("hello");
        ByteBuffer b2 = StandardCharsets.UTF_8.encode("world");
        ByteBuffer b3 = StandardCharsets.UTF_8.encode("李可龙");

        try (FileChannel fileChannel = new RandomAccessFile("words2.txt", "rw").getChannel()) {
            fileChannel.write(new ByteBuffer[]{b1, b2, b3});
        } catch (IOException e) {
        }
    }
}
