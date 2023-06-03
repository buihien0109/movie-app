package com.example.movieapp.service;

import com.example.movieapp.model.Film;

import java.util.List;

public interface FilmService {
    List<Film> findFilmsByGenre(String genre);

    Film findFilmById(Integer id);

    List<Film> getHotFilms(Integer limit);

    List<Film> getNewFilms(Integer limit);

    List<Film> getGenreFilmLatest(String gener, Integer limit);
}
