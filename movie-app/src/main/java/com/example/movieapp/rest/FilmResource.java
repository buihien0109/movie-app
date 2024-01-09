package com.example.movieapp.rest;

import com.example.movieapp.model.request.CreateFilmRequest;
import com.example.movieapp.model.request.UpdateFilmRequest;
import com.example.movieapp.service.FilmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("api/admin/films")
@RequiredArgsConstructor
public class FilmResource {
    private final FilmService filmService;

    @GetMapping
    public ResponseEntity<?> getAllFilms() {
        return ResponseEntity.ok(filmService.getAllFilms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFilmById(@PathVariable Integer id) {
        return ResponseEntity.ok(filmService.getFilmById(id));
    }

    @PostMapping
    public ResponseEntity<?> createFilm(@Valid @RequestBody CreateFilmRequest request) {
        log.info("Request to create film: {}", request);
        return new ResponseEntity<>(filmService.saveFilm(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFilm(@PathVariable Integer id, @Valid @RequestBody UpdateFilmRequest request) {
        log.info("Request to update film: {}", request);
        return ResponseEntity.ok(filmService.updateFilm(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFilm(@PathVariable Integer id) {
        filmService.deleteFilm(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/update-poster")
    public ResponseEntity<?> updatePoster(@RequestParam("file") MultipartFile file, @PathVariable Integer id) {
        return ResponseEntity.ok(filmService.updatePoster(id, file));
    }
}
