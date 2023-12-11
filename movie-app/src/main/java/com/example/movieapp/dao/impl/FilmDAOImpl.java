package com.example.movieapp.dao.impl;

import com.example.movieapp.dao.FilmDAO;
import com.example.movieapp.database.FilmDB;
import com.example.movieapp.model.Film;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class FilmDAOImpl implements FilmDAO {

    @Override
    public List<Film> findAll() {
        return FilmDB.filmList;
    }

    @Override
    public List<Film> findByType(String type) {
        return FilmDB.filmList.stream()
                .filter(film -> film.getType().equals(type))
                .toList();
    }

    @Override
    public Optional<Film> findById(Integer id) {
        return FilmDB.filmList.stream()
                .filter(film -> Objects.equals(film.getId(), id))
                .findFirst();
    }
}
