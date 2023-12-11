package com.example.movieapp.controller;

import com.example.movieapp.model.Actor;
import com.example.movieapp.model.Film;
import com.example.movieapp.response.PageResponse;
import com.example.movieapp.service.ActorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/actors")
public class ActorController {
    private final ActorService actorService;

    @GetMapping
    public String viewHomePage(Model model,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "5") int size) {
        PageResponse<Film> pageData = actorService.getAllActors(page, size);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageData.getTotalPages());
        model.addAttribute("totalItems", pageData.getTotalElements());
        model.addAttribute("listFilms", pageData.getContent());
        return "admin/actor/index";
    }

    @GetMapping("/showNewActorForm")
    public String showNewActorForm(Model model) {
        Actor actor = new Actor();
        model.addAttribute("actor", actor);
        return "admin/actor/create";
    }


    @PostMapping("/saveActor")
    public String saveProduct(@Valid @ModelAttribute("actor") Actor actor, BindingResult result, @RequestParam("image") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/actor/create";
        }

        try {
            actorService.saveActor(actor, file);
        } catch (IOException e) {
            log.error(e.getMessage());
            redirectAttributes.addFlashAttribute("message", "Upload failed!");
            return "admin/actor/create";
        }

        return "redirect:/admin/actors";
    }

    @GetMapping("/edit/{id}")
    public String showEditActorForm(@PathVariable Integer id, Model model) {
        Actor actor = actorService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid actor Id:" + id));
        model.addAttribute("actor", actor);
        return "admin/actor/detail";
    }

    @PostMapping("/updateActor")
    public String updateActor(@Valid @ModelAttribute("actor") Actor actor, BindingResult result, @RequestParam("image") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/actor/detail";
        }

        try {
            actorService.updateActor(actor, file);
        } catch (IOException e) {
            log.error(e.getMessage());
            redirectAttributes.addFlashAttribute("message", "Upload failed!");
            return "admin/actor/detail";
        }

        return "redirect:/admin/actors";
    }

    @GetMapping("/delete/{id}")
    public String deleteActor(@PathVariable Integer id) {
        actorService.deleteActor(id);
        return "redirect:/admin/actors";
    }
}
