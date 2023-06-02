package com.example.movieapp.controller;

import com.example.movieapp.model.Film;
import com.example.movieapp.service.FilmService;
import com.example.movieapp.utils.JsonFileReader;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@AllArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping("/")
    public String getHome(Model model) {
        List<Film> hotFilmList = filmService.getHotFilms(8);
        List<Film> newFilmList = filmService.getNewFilms(6);
        List<Film> featureFilmList = filmService.getGenreFilmLastest("phim-le", 6);

        model.addAttribute("hotFilmList", hotFilmList);
        model.addAttribute("newFilmList", newFilmList);
        model.addAttribute("featureFilmList", featureFilmList);
        return "index";
    }

    @GetMapping("/phim-le")
    public String getPhimLe(Model model) {
        List<Film> filmList = filmService.findFilmsByGenre("phim-le");
        model.addAttribute("filmList", filmList);
        return "phim-le";
    }

    @GetMapping("/phim-bo")
    public String getPhimMoi(Model model) {
        List<Film> filmList = filmService.findFilmsByGenre("phim-bo");
        model.addAttribute("filmList", filmList);
        return "phim-bo";
    }

    @GetMapping("/phim-chieu-rap")
    public String getPhimChieuRap(Model model) {
        List<Film> filmList = filmService.findFilmsByGenre("phim-chieu-rap");
        model.addAttribute("filmList", filmList);
        return "phim-chieu-rap";
    }

    @GetMapping("/phim/{id}")
    public String getPhim(Model model, @PathVariable Integer id) {
        Film film = filmService.findFilmById(id);
        model.addAttribute("film", film);
        return "chi-tiet";
    }
}

