package com.example.movieapp.controller;

import com.example.movieapp.entity.Country;
import com.example.movieapp.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/countries")
@RequiredArgsConstructor
public class CountryController {
    private final CountryService countryService;

    @GetMapping
    public String getHomePage(Model model) {
        List<Country> countryList = countryService.getAllCountries();
        model.addAttribute("countryList", countryList);
        return "admin/country/index";
    }
}
