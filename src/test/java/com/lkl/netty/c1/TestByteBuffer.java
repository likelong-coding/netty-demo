package com.lkl.netty.c1;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * ByteBuffer基本使用
 * @author likelong
 * @date 2022/12/14
 */
@Slf4j
public class TestByteBuffer {

    public static void main(String[] args) {
        //得到FileChannel
        //1、输入输出流 2、RandomAccessFile
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            //准备缓冲区 固定10个字节
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while (true) {
                //每次读取字节数，读取结束返回-1 （从channel读取数据，向buffer写入）
                int len = channel.read(buffer);
                log.debug("读取到的字节数：{}", len);
                if (len == -1) { //没有内容了，读取结束
                    break;
                }
                // 切换为读模式
                buffer.flip();
                //是否还有剩余未读数据
                while (buffer.hasRemaining()) {
                    log.debug("实际字节：{}", (char) buffer.get());
                }
                // 切换为写模式
                buffer.clear();
            }
        } catch (IOException e) {
            log.error("exception : {}", e.getMessage());
        }
    }
}
