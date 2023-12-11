package com.example.movieapp.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class JsonFileReader<T> implements FileReader<T> {
    private final ResourceLoader resourceLoader;

    public JsonFileReader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public List<T> readFile(String filePath, Class<T> tClass) {
        try {
            Resource resource = resourceLoader.getResource(filePath);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(resource.getFile(), objectMapper.getTypeFactory().constructCollectionType(List.class, tClass));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
