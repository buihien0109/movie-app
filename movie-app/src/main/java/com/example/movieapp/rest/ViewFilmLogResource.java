package com.example.movieapp.rest;

import com.example.movieapp.service.ViewFilmLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/view-film-logs")
@RequiredArgsConstructor
public class ViewFilmLogResource {
    private final ViewFilmLogService viewFilmLogService;

    @PostMapping
    public ResponseEntity<?> createViewFilmLog(@Valid @RequestParam Integer filmId) {
        viewFilmLogService.createViewFilmLog(filmId);
        return ResponseEntity.noContent().build();
    }
}
