package com.example.movieapp.utils;

import java.util.List;

public interface FileReader<T> {
    List<T> readFile(String filePath, Class<T> tClass);
}
