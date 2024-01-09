package com.example.movieapp.rest;

import com.example.movieapp.model.request.UpsertEpisodeRequest;
import com.example.movieapp.model.request.UpsertGenreRequest;
import com.example.movieapp.service.EpisodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("api/admin/episodes")
@RequiredArgsConstructor
public class EpisodeResource {
    private final EpisodeService episodeService;

    @PostMapping
    public ResponseEntity<?> createEpisode(@Valid @RequestBody UpsertEpisodeRequest request) {
        log.info("Request to create episode: {}", request);
        return new ResponseEntity<>(episodeService.saveEpisode(request), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/upload-video")
    public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile file, @PathVariable Integer id) {
        return ResponseEntity.ok(episodeService.uploadVideo(id, file));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEpisode(@PathVariable Integer id, @Valid @RequestBody UpsertEpisodeRequest request) {
        return ResponseEntity.ok(episodeService.updateEpisode(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEpisode(@PathVariable Integer id) {
        episodeService.deleteEpisode(id);
        return ResponseEntity.noContent().build();
    }
}
