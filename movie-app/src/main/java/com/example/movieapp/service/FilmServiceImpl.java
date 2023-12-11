package com.example.movieapp.service;

import com.example.movieapp.constant.FilmConstant;
import com.example.movieapp.model.Film;
import com.example.movieapp.response.PageResponse;
import com.example.movieapp.response.PageResponseImpl;
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
    public List<Film> findFilmsByType(String type, String sortBy) {
        log.info("type : {}", type);
        log.info("sortBy : {}", sortBy);

        List<Film> films = findFilmsByType(type);
        log.info("films : {}", films);

        if (sortBy == null) {
            return films;
        }
        return switch (sortBy) {
            case "releaseYear" -> films.stream()
                    .sorted(Comparator.comparing(Film::getReleaseYear).reversed())
                    .toList();
            case "view" -> films.stream()
                    .sorted(Comparator.comparing(Film::getView).reversed())
                    .toList();
            case "rating" -> films.stream()
                    .sorted(Comparator.comparing(Film::getRating).reversed())
                    .toList();
            default -> films;
        };
    }

    @Override
    public PageResponse<Film> findFilmsByType(String type, Integer page) {
        List<Film> films = filmList.stream()
                .filter(film -> film.getType().equals(type))
                .toList();

        return new PageResponseImpl<>(films, page, FilmConstant.FILM_PER_PAGE);
    }

    @Override
    public PageResponse<Film> findFilmsByType(String type, String sortBy, Integer page) {
        log.info("type : {}", type);
        log.info("sortBy : {}", sortBy);
        log.info("page : {}", page);

        List<Film> films = findFilmsByType(type, sortBy);
        log.info("films : {}", films);

        return new PageResponseImpl<>(films, page, FilmConstant.FILM_PER_PAGE);
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
