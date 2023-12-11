package com.example.movieapp.dao;

import com.example.movieapp.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmDAO {
    List<Film> findAll();

    List<Film> findByType(String type);

    Optional<Film> findById(Integer id);
}
