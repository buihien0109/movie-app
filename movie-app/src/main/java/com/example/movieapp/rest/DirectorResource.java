package com.example.movieapp.rest;

import com.example.movieapp.model.request.CreateDirectorRequest;
import com.example.movieapp.model.request.UpdateDirectorRequest;
import com.example.movieapp.service.DirectorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("api/admin/directors")
@RequiredArgsConstructor
public class DirectorResource {
    private final DirectorService directorService;

    @GetMapping
    public ResponseEntity<?> getAllDirectors() {
        return ResponseEntity.ok(directorService.getAllDirectors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDirectorById(@PathVariable Integer id) {
        return ResponseEntity.ok(directorService.getDirectorById(id));
    }

    @PostMapping
    public ResponseEntity<?> createDirector(@Valid @RequestBody CreateDirectorRequest request) {
        return new ResponseEntity<>(directorService.saveDirector(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDirector(@PathVariable Integer id, @Valid @RequestBody UpdateDirectorRequest request) {
        return ResponseEntity.ok(directorService.updateDirector(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDirector(@PathVariable Integer id) {
        directorService.deleteDirector(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/update-avatar")
    public ResponseEntity<?> updateAvatar(@RequestParam("file") MultipartFile file, @PathVariable Integer id) {
        return ResponseEntity.ok(directorService.updateAvatar(id, file));
    }
}
