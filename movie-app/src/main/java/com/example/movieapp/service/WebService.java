package com.example.movieapp.service;

import com.example.movieapp.entity.Blog;
import com.example.movieapp.entity.Episode;
import com.example.movieapp.entity.Film;
import com.example.movieapp.entity.Review;
import com.example.movieapp.model.dto.*;
import com.example.movieapp.model.enums.FilmAccessType;
import com.example.movieapp.model.enums.FilmType;
import com.example.movieapp.repository.BlogRepository;
import com.example.movieapp.repository.EpisodeRepository;
import com.example.movieapp.repository.FilmRepository;
import com.example.movieapp.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebService {
    private final FilmRepository filmRepository;
    private final BlogRepository blogRepository;
    private final EpisodeRepository episodeRepository;
    private final ReviewRepository reviewRepository;

    public Page<FilmDto> getFilmsByType(FilmType type, FilmAccessType accessType, Boolean status, Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("publishedAt").descending());
        return filmRepository.findByTypeAndAccessTypeAndStatus(type, accessType, true, pageable);
    }

    private Film findFilmById(Integer filmId) {
        return filmRepository.findById(filmId).orElse(null);
    }

    public Film findFilmByIdAndSlug(Integer id, String slug, FilmAccessType accessType) {
        return filmRepository.findByIdAndSlugAndAccessType(id, slug, accessType).orElse(null);
    }

    public List<FilmDto> getPhimHot(Integer limit) {
        List<FilmDto> filmDtoList = filmRepository.findPhimHot();
        if (filmDtoList.size() > limit) {
            return filmDtoList.subList(0, limit);
        }
        return filmDtoList;
    }

    public List<FilmDto> getRelateFilms(Integer filmId, Integer limit) {
        Film film = findFilmById(filmId);
        if (film == null) {
            return new ArrayList<>();
        }
        return filmRepository.findRelateFilms(film.getType(), film.getAccessType(), true, filmId, PageRequest.of(0, limit)).getContent();
    }

    public List<Film> getRelateProFilms(Integer filmId, Integer limit) {
        Film film = findFilmById(filmId);
        if (film == null) {
            return new ArrayList<>();
        }
        return filmRepository.findRelateProFilms(film.getType(), film.getAccessType(), true, filmId, PageRequest.of(0, limit)).getContent();
    }

    public Page<BlogDto> getAllBlogs(Integer page, Integer limit) {
        return blogRepository.findByStatus(true, PageRequest.of(page - 1, limit, Sort.by("publishedAt").descending()));
    }

    // get blog by id and slug
    public BlogDetailDto getBlogByIdAndSlug(Integer id, String slug) {
        return blogRepository.findByIdAndSlugAndStatus(id, slug, true).orElse(null);
    }

    public List<EpisodeDto> getEpisodesByFilmId(Integer filmId) {
        return episodeRepository.findByFilm_IdAndStatusOrderByDisplayOrderAsc(filmId, true);
    }

    public List<ReviewDto> getReviewsOfFilm(Integer filmId) {
        return reviewRepository.findByFilm_IdOrderByCreatedAtDesc(filmId);
    }

    public EpisodeDto getEpisodeByDisplayOrder(Integer filmId, Boolean status, String tap) {
        log.info("tap: {}", tap);
        if (tap == null) {
            return null;
        }
        if (tap.equals("full")) {
            return episodeRepository.findByFilm_IdAndStatusAndDisplayOrder(filmId, status, 1).orElse(null);
        } else {
            return episodeRepository.findByFilm_IdAndStatusAndDisplayOrder(filmId, status, Integer.parseInt(tap)).orElse(null);
        }
    }

    public Page<FilmDto> getFilmsByAccessType(FilmAccessType filmAccessType, boolean status, Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("publishedAt").descending());
        return filmRepository.findByAccessTypeAndStatus(filmAccessType, status, pageable);
    }
}
