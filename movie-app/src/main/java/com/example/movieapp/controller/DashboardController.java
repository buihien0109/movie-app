package com.example.movieapp.controller;

import com.example.movieapp.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class DashboardController {
    private final DashboardService dashboardService;
    @GetMapping
    public String getAdminPage(Model model) {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String getDashboardPage(Model model) {
        model.addAttribute("revenue", dashboardService.getRevenueByMonth(5));
        model.addAttribute("topViewFilms", dashboardService.getTopViewFilms(10));
        model.addAttribute("latestOrders", dashboardService.getLatestOrders(10));
        model.addAttribute("latestUsers", dashboardService.getLatestUsers(10));
        model.addAttribute("countFilm", dashboardService.countLatestFilms());
        model.addAttribute("countOrder", dashboardService.countLatestOrders());
        model.addAttribute("countUser", dashboardService.countLatestUsers());
        model.addAttribute("countBlog", dashboardService.countLatestBlogs());
        return "admin/dashboard/index";
    }
}
