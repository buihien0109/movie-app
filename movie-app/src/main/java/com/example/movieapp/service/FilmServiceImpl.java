package com.example.movieapp.service;

import com.example.movieapp.constant.FilmConstant;
import com.example.movieapp.model.Film;
import com.example.movieapp.utils.FileReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {
    private final List<Film> filmList;

    public FilmServiceImpl(FileReader fileReader) {
        this.filmList = fileReader.readFile(FilmConstant.FILM_FILE_PATH);
    }

    @Override
    public List<Film> findFilmsByType(String type) {
        return filmList.stream()
                .filter(film -> film.getType().equals(type))
                .toList();
    }

    @Override
    public Film findFilmById(Integer id) {
        return filmList.stream()
                .filter(f -> f.getId().equals(id))
                .findFirst().orElseThrow(() -> new RuntimeException("Not found"));
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
    public List<Film> getRelateFilms(Integer filmId, Integer limit) {
        Film film = findFilmById(filmId);
        return filmList.stream()
                .filter(f -> !f.getId().equals(filmId))
                .filter(f -> f.getType().equals(film.getType()))
                .limit(limit)
                .toList();
    }


    @Override
    public List<Film> getTypeFilmLatest(String gener, Integer limit) {
        return filmList.stream()
                .filter(film -> film.getType().equals(gener))
                .sorted(Comparator.comparing(Film::getReleaseYear).reversed())
                .limit(limit)
                .toList();
    }

    @Override
    public List<Film> searchFilm(String name, Integer releaseYear) {
        log.info("name : {}", name);
        log.info("releaseYear : {}", releaseYear);

        if ((name == null && releaseYear == null) || (Objects.equals(name, "") && releaseYear == null)) {
            return new ArrayList<>();
        }

        return filmList.stream()
                .filter(film ->
                        (name == null || film.getTitle().toLowerCase().contains(name.toLowerCase()))
                                && (releaseYear == null || film.getReleaseYear().equals(releaseYear))
                )
                .toList();
    }
}
