package com.example.movieapp.controller;

import com.example.movieapp.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/blogs")
public class BlogController {
    private final BlogService blogService;

    // Danh sách tất cả bài viết
    @GetMapping
    public String getHomePage(Model model) {
        return "admin/blog/index";
    }

    // Danh sách bài viết của tôi
    @GetMapping("/own-blogs")
    public String getOwnPage(Model model) {
        return "admin/blog/own-blog";
    }

    // Tạo bài viết
    @GetMapping("/create")
    public String getCreatePage(Model model) {
        return "admin/blog/create";
    }

    // Chi tiết bài viết
    @GetMapping("/{id}/detail")
    public String getDetailPage(@PathVariable Integer id, Model model) {
        model.addAttribute("blog", blogService.getBlogById(id));
        return "admin/blog/detail";
    }
}
