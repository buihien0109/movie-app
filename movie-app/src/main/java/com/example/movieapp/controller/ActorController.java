package com.example.movieapp.controller;

import com.example.movieapp.entity.Actor;
import com.example.movieapp.service.ActorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/actors")
public class ActorController {
    private final ActorService actorService;

    @GetMapping
    public String getHomePage(Model model) {
        List<Actor> actorList = actorService.getAllActors();
        model.addAttribute("actorList", actorList);
        return "admin/actor/index";
    }

    @GetMapping("/create")
    public String getCreatePage() {
        return "admin/actor/create";
    }

    @GetMapping("/{id}/detail")
    public String getDetailPage(@PathVariable Integer id, Model model) {
        model.addAttribute("actor", actorService.getActorById(id));
        return "admin/actor/detail";
    }
}
