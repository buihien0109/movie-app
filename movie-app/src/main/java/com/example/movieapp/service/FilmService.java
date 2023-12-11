package com.example.movieapp.service;

import com.example.movieapp.model.Film;
import com.example.movieapp.response.PageResponse;

import java.util.List;

public interface FilmService {
    List<Film> findFilmsByType(String type);

    List<Film> findFilmsByType(String type, String sortBy);

    PageResponse<Film> findFilmsByType(String type, Integer page);

    PageResponse<Film> findFilmsByType(String type, String sortBy, Integer page);

    Film findFilmById(Integer id);

    List<Film> getHotFilms(Integer limit);

    List<Film> getNewFilms(Integer limit);

    List<Film> getRelateFilms(Integer filmId, Integer limit);

    List<Film> getTypeFilmLatest(String type, Integer limit);

    List<Film> searchFilm(String name, Integer releaseYear);
}
