package com.example.movieapp.controller;

import com.example.movieapp.entity.Film;
import com.example.movieapp.model.enums.FilmType;
import com.example.movieapp.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final DirectorService directorService;
    private final GenreService genreService;
    private final ActorService actorService;
    private final ReviewService reviewService;
    private final EpisodeService episodeService;
    private final CountryService countryService;

    @GetMapping
    public String getHomePage(Model model) {
        List<Film> filmList = filmService.getAllFilms();
        model.addAttribute("filmList", filmList);
        return "admin/film/index";
    }

    @GetMapping("/create")
    public String getCreatePage(Model model) {
        model.addAttribute("directorList", directorService.getAllDirectors());
        model.addAttribute("genreList", genreService.getAllGenres());
        model.addAttribute("actorList", actorService.getAllActors());
        model.addAttribute("typeList", FilmType.values());
        model.addAttribute("countryList", countryService.getAllCountries());
        return "admin/film/create";
    }

    @GetMapping("/{id}/detail")
    public String getDetailPage(@PathVariable Integer id, Model model) {
        model.addAttribute("film", filmService.getFilmById(id));
        model.addAttribute("episodeList", episodeService.getAllEpisodesOfFilm(id));
        model.addAttribute("reviewList", reviewService.getReviewsOfFilm(id));
        model.addAttribute("directorList", directorService.getAllDirectors());
        model.addAttribute("genreList", genreService.getAllGenres());
        model.addAttribute("actorList", actorService.getAllActors());
        model.addAttribute("typeList", FilmType.values());
        model.addAttribute("countryList", countryService.getAllCountries());
        return "admin/film/detail";
    }
}
