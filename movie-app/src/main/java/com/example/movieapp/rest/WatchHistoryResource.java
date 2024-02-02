package com.example.movieapp.rest;

import com.example.movieapp.model.request.WatchHistoryRequest;
import com.example.movieapp.service.WatchHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watch-history")
@RequiredArgsConstructor
public class WatchHistoryResource {
    private final WatchHistoryService watchHistoryService;

    @PostMapping
    public ResponseEntity<?> watchFilm(@Valid @RequestBody WatchHistoryRequest request) {
        watchHistoryService.saveWatchFilm(request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWatchFilm(@PathVariable Integer id) {
        watchHistoryService.deleteWatchFilm(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/delete-all")
    public ResponseEntity<?> deleteAllWatchFilm() {
        watchHistoryService.deleteAllWatchFilm();
        return ResponseEntity.noContent().build();
    }
}
