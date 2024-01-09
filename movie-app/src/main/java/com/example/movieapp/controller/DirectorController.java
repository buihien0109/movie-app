package com.example.movieapp.controller;

import com.example.movieapp.entity.Director;
import com.example.movieapp.service.DirectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/directors")
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public String getHomePage(Model model) {
        List<Director> directorList = directorService.getAllDirectors();
        model.addAttribute("directorList", directorList);
        return "admin/director/index";
    }

    @GetMapping("/create")
    public String getCreatePage() {
        return "admin/director/create";
    }

    @GetMapping("/{id}/detail")
    public String getDetailPage(@PathVariable Integer id, Model model) {
        model.addAttribute("director", directorService.getDirectorById(id));
        return "admin/director/detail";
    }
}
