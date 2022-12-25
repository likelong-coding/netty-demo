package com.lkl.netty.c4;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.lkl.netty.utils.ByteBufferUtil.debugAll;

/**
 * @author likelong
 * @date 2022/12/24
 */
@Slf4j
public class MultiThreadServer {

    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        Selector boss = Selector.open();
        SelectionKey bossKey = ssc.register(boss, 0, null);
        // boss 线程只关注 accept事件
        bossKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));

        // 1、初始化固定数量 Worker 并初始化
//        Worker worker = new Worker("worker-0");

        // 一般创建数量为主机可用核心数
        Worker[] workers = new Worker[2];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("worker-" + i);
        }
        // 计数器 平均分配到 worker上，轮询
        AtomicInteger index = new AtomicInteger(0);
        while (true) {
            boss.select();
            Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    log.debug("connected... {}", sc.getRemoteAddress());
                    // 2、关联 selector
                    log.debug("before register... {}", sc.getRemoteAddress());
                    // 负载均衡，轮询分配Worker
                    workers[index.getAndIncrement() % workers.length].register(sc); // boss
                    // 保证 sc.register(worker.selector, SelectionKey.OP_READ, null); 在Worker selector.select() 之后执行
                    log.debug("after register... {}", sc.getRemoteAddress());
                }
            }
        }
    }

    static class Worker implements Runnable {
        private Thread thread;
        private Selector selector;
        private String name;
        private volatile boolean start = false; // 还未初始化

        // 两个队列传递一些数据，可以使用队列
        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

        public Worker(String name) {
            this.name = name;
        }

        // 初始化
        public void register(SocketChannel sc) throws IOException {
            if (!start) {
                // 一个线程只初始化一个线程
                thread = new Thread(this, name);
                selector = Selector.open();
                thread.start();
                start = true;
            }

            // 主线程中添加任务，任务还未执行
//            queue.offer(() -> {
//                try {
//                    sc.register(selector, SelectionKey.OP_READ, null);
//                } catch (ClosedChannelException e) {
//                    e.printStackTrace();
//                }
//            });

            // 手动唤醒
            selector.wakeup(); // 无论是在 selector.select() 之前或者之后运行，都不会让 selector.select() 阻塞
            sc.register(selector, SelectionKey.OP_READ, null);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    selector.select(); // worker-0
//                    Runnable task = queue.poll();
//                    if(task != null){
//                        task.run(); // 在Worker 线程中执行 sc.register(worker.selector, SelectionKey.OP_READ, null)
//                    }
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        // Worker只负责Read事件
                        if (key.isReadable()) {
                            // 简化处理，省略细节
                            SocketChannel sc = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            log.debug("read... {}", sc.getRemoteAddress());
                            sc.read(buffer);
                            buffer.flip();
                            debugAll(buffer);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
