package com.example.movieapp.controller;

import com.example.movieapp.constant.FilmConstant;
import com.example.movieapp.model.Film;
import com.example.movieapp.service.FilmService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping("/")
    public String getHome(Model model) {
        List<Film> hotFilmList = filmService.getHotFilms(8);
        List<Film> newFilmList = filmService.getNewFilms(6);
        List<Film> featureFilmList = filmService.getTypeFilmLatest(FilmConstant.PHIM_LE, 6);

        model.addAttribute("hotFilmList", hotFilmList);
        model.addAttribute("newFilmList", newFilmList);
        model.addAttribute("featureFilmList", featureFilmList);
        return "index";
    }

    @GetMapping("/phim-le")
    public String getPhimLe(Model model) {
        List<Film> filmList = filmService.findFilmsByType(FilmConstant.PHIM_LE);
        model.addAttribute("filmList", filmList);
        return "phim-le";
    }

    @GetMapping("/phim-bo")
    public String getPhimMoi(Model model) {
        List<Film> filmList = filmService.findFilmsByType(FilmConstant.PHIM_BO);
        model.addAttribute("filmList", filmList);
        return "phim-bo";
    }

    @GetMapping("/phim-chieu-rap")
    public String getPhimChieuRap(Model model) {
        List<Film> filmList = filmService.findFilmsByType(FilmConstant.PHIM_CHIEU_RAP);
        model.addAttribute("filmList", filmList);
        return "phim-chieu-rap";
    }

    @GetMapping("/phim/{id}")
    public String getPhim(Model model, @PathVariable Integer id) {
        Film film = filmService.findFilmById(id);
        List<Film> relateFilmList = filmService.getRelateFilms(id, 6);

        model.addAttribute("film", film);
        model.addAttribute("relateFilmList", relateFilmList);
        return "chi-tiet";
    }

    @GetMapping("/tim-kiem")
    public String searchFilm(Model model,
                             @RequestParam(required = false) String name,
                             @RequestParam(required = false) Integer releaseYear) {
        List<Film> filmList = filmService.searchFilm(name, releaseYear);
        model.addAttribute("filmList", filmList);
        return "tim-kiem";
    }
}

