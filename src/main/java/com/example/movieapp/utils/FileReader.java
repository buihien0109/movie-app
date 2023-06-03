package com.example.movieapp.utils;

import com.example.movieapp.model.Film;

import java.util.List;

public interface FileReader {
    List<Film> readFile(String filePath);
}
