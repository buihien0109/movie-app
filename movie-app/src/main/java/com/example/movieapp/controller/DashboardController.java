package com.example.movieapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class DashboardController {
    @GetMapping
    public String getAdminPage(Model model) {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String getDashboardPage(Model model) {
        return "admin/dashboard/index";
    }
}
