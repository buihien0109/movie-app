package com.example.movieapp.service;

import com.example.movieapp.model.Film;
import com.example.movieapp.utils.FileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class FilmServiceImpl implements FilmService{
    private List<Film> filmList;
    private final FileReader fileReader;

    @Autowired
    public FilmServiceImpl(FileReader fileReader) {
        this.fileReader = fileReader;
        this.filmList = this.fileReader.readFile("classpath:static/data.json");
    }

    @Override
    public List<Film> findFilmsByGenre(String genre) {
        return filmList.stream()
                .filter(film -> film.getGenre().equals(genre))
                .toList();
    }

    @Override
    public Film findFilmById(Integer id) {
        Film film = filmList.stream()
                .filter(f -> f.getId().equals(id))
                .findFirst().orElseThrow(() -> {
                    throw new RuntimeException("Not found");
                });
        return film;
    }

    @Override
    public List<Film> getHotFilms(Integer limit) {
        return filmList.stream()
                .sorted(Comparator.comparing(Film::getView).reversed())
                .limit(limit)
                .toList();
    }

    @Override
    public List<Film> getNewFilms(Integer limit) {
        return filmList.stream()
                .sorted(Comparator.comparing(Film::getReleaseYear).reversed())
                .limit(limit)
                .toList();
    }

    @Override
    public List<Film> getGenreFilmLatest(String gener, Integer limit) {
        return filmList.stream()
                .filter(film -> film.getGenre().equals(gener))
                .sorted(Comparator.comparing(Film::getReleaseYear).reversed())
                .limit(limit)
                .toList();
    }
}
