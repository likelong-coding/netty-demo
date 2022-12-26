package com.lkl.nio.c4;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static com.lkl.nio.utils.ByteBufferUtil.debugAll;

/**
 * @author likelong
 * @date 2022/12/25
 */
@Slf4j
public class AioFileChannel {

    public static void main(String[] args) {
        // 文件异步io，一个线程读取文件直接返回，读取完毕后另一个线程调用上一个线程回调函数返回读取结果，因此异步io至少需要两个线程
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get("data.txt"), StandardOpenOption.READ)) {

            ByteBuffer buffer = ByteBuffer.allocate(16);
            // 参数1 ByteBuffer
            // 参数2 读取的起始位置
            // 参数3 附件
            // 参数4 回调对象 CompletionHandler
            log.debug("read begin...");
            channel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override  // 读取成功回调 调用回调函数的线程为守护线程，主线程结束守护线程也随之结束
                public void completed(Integer result, ByteBuffer attachment) {
                      log.debug("read completed...{}", result);
                      attachment.flip(); // 切换读模式
                      debugAll(attachment);
                }

                @Override // 读取失败回调
                public void failed(Throwable exc, ByteBuffer attachment) {

                }
            });

            log.debug("read end...");

            // 主线程阻塞查看回调结果
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
