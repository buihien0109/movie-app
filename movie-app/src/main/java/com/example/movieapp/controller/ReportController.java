package com.example.movieapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/reports")
public class ReportController {
    @GetMapping
    public String getHomePage(Model model) {
        return "admin/report/index";
    }
}
