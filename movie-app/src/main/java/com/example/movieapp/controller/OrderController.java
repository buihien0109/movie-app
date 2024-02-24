package com.example.movieapp.controller;

import com.example.movieapp.model.enums.FilmAccessType;
import com.example.movieapp.model.enums.OrderPaymentMethod;
import com.example.movieapp.service.FilmService;
import com.example.movieapp.service.OrderService;
import com.example.movieapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
public class OrderController {
    private final OrderService orderService;
    private final FilmService filmService;
    private final UserService userService;

    @GetMapping
    public String getHomePage() {
        return "admin/order/index";
    }

    @GetMapping("/create")
    public String getCreatePage(Model model) {
        model.addAttribute("films", filmService.getAllFilmsByAccessType(FilmAccessType.PAID));
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("paymentMethods", OrderPaymentMethod.values());
        return "admin/order/create";
    }

    @GetMapping("/{id}/detail")
    public String getDetailPage(@PathVariable Integer id, Model model) {
        model.addAttribute("order", orderService.getOrderById(id));
        model.addAttribute("films", filmService.getAllFilmsByAccessType(FilmAccessType.PAID));
        model.addAttribute("paymentMethods", OrderPaymentMethod.values());
        return "admin/order/detail";
    }
}
