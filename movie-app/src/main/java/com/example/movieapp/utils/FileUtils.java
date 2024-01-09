package com.example.movieapp.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileUtils {
    public static void createDirectory(String name) {
        Path path = Paths.get(name);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                log.error("Không thể tạo thư mục upload");
                log.error(e.getMessage());
                throw new RuntimeException("Could not create upload folder");
            }
        }
    }

    public static void deleteFile(String videoPath) {
        Path path = Paths.get(videoPath);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("Không thể xóa file");
            log.error(e.getMessage());
            throw new RuntimeException("Could not delete file");
        }
    }
}
