package com.example.movieapp.database;

import com.example.movieapp.constant.FilmConstant;
import com.example.movieapp.model.Film;
import com.example.movieapp.utils.FileReader;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class InitDB implements CommandLineRunner {
    private final FileReader fileReader;

    @Override
    public void run(String... args) throws Exception {
        FilmDB.filmList = fileReader.readFile(FilmConstant.FILM_FILE_PATH, Film.class);
    }
}
