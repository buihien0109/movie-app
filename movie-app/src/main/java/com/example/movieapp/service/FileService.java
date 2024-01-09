package com.example.movieapp.service;

import com.example.movieapp.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
public class FileService {
    public static final String uploadDir = "image_uploads";

    public FileService() {
        FileUtils.createDirectory(uploadDir);
    }

    public String saveFile(MultipartFile multipartFile) {
        // file name is miliseconds
        String fileName = String.valueOf(System.currentTimeMillis());

        Path uploadPath = Paths.get(uploadDir);
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            return "/" + uploadDir + "/" + fileName;
        } catch (IOException e) {
            log.error("Không thể lưu file");
            log.error(e.getMessage());
        }
        return null;
    }

    public void deleteFile(String filePath) {
        // filePath: /image_uploads/123456789
        filePath = filePath.substring(1);
        FileUtils.deleteFile(filePath);
    }
}
