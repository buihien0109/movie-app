package com.example.movieapp;

import com.example.movieapp.model.Film;
import com.example.movieapp.service.FilmService;
import com.example.movieapp.service.FilmServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MovieAppApplicationTests {

    @Autowired
    private FilmService filmService;

    @Test
    void searchFilm_Test() {
        List<Film> filmList = filmService.searchFilm("an", 2023);

        System.out.println(filmList.size());
        filmList.forEach(film -> System.out.println(film.getReleaseYear() + " " + film.getTitle()));
    }
}
