package com.shulng.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileStorageConfig {

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${upload.cover-path}")
    private String coverPath;

    @PostConstruct
    public void init() throws IOException {
        createDirectoryIfNotExists(uploadPath);
        createDirectoryIfNotExists(coverPath);
        createDirectoryIfNotExists("./data");
    }

    private void createDirectoryIfNotExists(String pathStr) throws IOException {
        Path path = Paths.get(pathStr);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }
}
