package com.example.movieapp.utils;

import com.example.movieapp.model.Film;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Component
public class JsonFileReader implements FileReader {
    private final ResourceLoader resourceLoader;

    public JsonFileReader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public List<Film> readFile(String filePath) {
        try {
            Resource resource = resourceLoader.getResource(filePath);
            String jsonContent = Files.readString(resource.getFile().toPath());
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonContent, new TypeReference<List<Film>>() {
            });
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
