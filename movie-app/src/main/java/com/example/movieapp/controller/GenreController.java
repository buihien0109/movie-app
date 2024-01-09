package com.example.movieapp.controller;

import com.example.movieapp.entity.Genre;
import com.example.movieapp.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public String getHomePage(Model model) {
        List<Genre> genreList = genreService.getAllGenres();
        model.addAttribute("genreList", genreList);
        return "admin/genre/index";
    }
}
