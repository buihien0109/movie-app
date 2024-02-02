package com.example.movieapp.rest;

import com.example.movieapp.model.request.UpsertGenreRequest;
import com.example.movieapp.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/admin/genres")
@RequiredArgsConstructor
public class GenreResource {
    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<?> getAllGenres() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGenreById(@PathVariable Integer id) {
        return ResponseEntity.ok(genreService.getGenreById(id));
    }

    @PostMapping
    public ResponseEntity<?> createGenre(@Valid @RequestBody UpsertGenreRequest request) {
        return new ResponseEntity<>(genreService.saveGenre(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGenre(@PathVariable Integer id, @Valid @RequestBody UpsertGenreRequest request) {
        return ResponseEntity.ok(genreService.updateGenre(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGenre(@PathVariable Integer id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}
