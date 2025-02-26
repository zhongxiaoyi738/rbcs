package com.hsbc.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Slf4j
public class FileUtils {
    public static void appendToFile(Path filePath, String content) {
        try {
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, content.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.error("文件追加失败", e);
        }
    }

    public static void cleanOld(Path dirPath, Integer history) {
        long cutoff = LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - TimeUnit.DAYS.toMillis(history);
        try (Stream<Path> files = Files.list(dirPath)) {
            files.filter(Files::isRegularFile)
                    .filter(p -> {
                        try {
                            return Files.getLastModifiedTime(p).toMillis() < cutoff;
                        } catch (IOException e) {
                            log.error("获取文件时间失败: {}", p, e);
                            return false;
                        }
                    })
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            log.error("删除文件失败: {}", p, e);
                        }
                    });
        } catch (IOException e) {
            log.error("目录访问失败: {}", dirPath, e);
        }
    }
}
