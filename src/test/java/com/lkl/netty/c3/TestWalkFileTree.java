package com.lkl.netty.c3;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author likelong
 * @date 2022/12/18
 */
public class TestWalkFileTree {
    public static void main(String[] args) throws IOException {
        String source = "D:\\Snipaste";
        String target = "D:\\Snipaste666";
        Files.walk(Paths.get(source)).forEach(path -> {
            try {
                String targetName = path.toString().replace(source, target);
                // 目录
                if (Files.isDirectory(path)) {
                    // 创建目录
                    Files.createDirectory(Paths.get(targetName));
                }
                // 普通文件
                else if (Files.isRegularFile(path)) {
                    // 拷贝文件
                    Files.copy(path, Paths.get(targetName));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void m3() throws IOException {
        Files.walkFileTree(Paths.get("D:\\Snipaste"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // 删除文件夹所有文件
                Files.delete(file);
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                // 退出文件夹时，文件夹已经为空可以直接删除
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }

    private static void m2() throws IOException {
        AtomicInteger jarCount = new AtomicInteger();
        Files.walkFileTree(Paths.get("D:\\environment\\java\\jdk1.8.0_333"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".jar")) {
                    System.out.println(file);
                    jarCount.incrementAndGet();
                }
                return super.visitFile(file, attrs);
            }
        });
        System.out.println("jar包数量：" + jarCount.get());
    }

    private static void m1() throws IOException {
        // 记录文件夹数量
        AtomicInteger dirCount = new AtomicInteger();
        // 记录文件数量
        AtomicInteger fileCount = new AtomicInteger();
        Files.walkFileTree(Paths.get("D:\\environment\\java\\jdk1.8.0_333"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("===> dir: " + dir);
                dirCount.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file);
                fileCount.incrementAndGet();
                return super.visitFile(file, attrs);
            }
        });

        // 打印数目
        System.out.println("文件目录数:" + dirCount.get());
        System.out.println("文件数:" + fileCount.get());
    }

}
