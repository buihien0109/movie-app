package com.example.movieapp.service;

import com.example.movieapp.model.Film;
import com.example.movieapp.utils.JsonFileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class FilmService {
    private List<Film> filmList;
    private final JsonFileReader jsonFileReader;

    @Autowired
    public FilmService(JsonFileReader jsonFileReader) {
        this.jsonFileReader = jsonFileReader;
        this.filmList = this.jsonFileReader.readJsonFile();
    }

    public List<Film> findFilmsByGenre(String genre) {
        return filmList.stream()
                .filter(film -> film.getGenre().equals(genre))
                .toList();
    }

    public Film findFilmById(Integer id) {
        Film film = filmList.stream()
                .filter(f -> f.getId().equals(id))
                .findFirst().orElseThrow(() -> {
                    throw new RuntimeException("Not found");
                });
        System.out.println(film);

        return film;
    }

    public List<Film> getHotFilms(Integer limit) {
        return filmList.stream()
                .sorted(Comparator.comparing(Film::getView).reversed())
                .limit(limit)
                .toList();
    }

    public List<Film> getNewFilms(Integer limit) {
        return filmList.stream()
                .sorted(Comparator.comparing(Film::getReleaseYear).reversed())
                .limit(limit)
                .toList();
    }

    public List<Film> getGenreFilmLastest(String gener, Integer limit) {
        return filmList.stream()
                .filter(film -> film.getGenre().equals(gener))
                .sorted(Comparator.comparing(Film::getReleaseYear).reversed())
                .limit(limit)
                .toList();
    }
}
