package com.example.movieapp.rest;

import com.example.movieapp.model.request.CreateActorRequest;
import com.example.movieapp.model.request.UpdateActorRequest;
import com.example.movieapp.service.ActorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("api/admin/actors")
@RequiredArgsConstructor
public class ActorResource {
    private final ActorService actorService;

    @GetMapping
    public ResponseEntity<?> getAllActors() {
        return ResponseEntity.ok(actorService.getAllActors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getActorById(@PathVariable Integer id) {
        return ResponseEntity.ok(actorService.getActorById(id));
    }

    @PostMapping
    public ResponseEntity<?> createActor(@Valid @RequestBody CreateActorRequest request) {
        return new ResponseEntity<>(actorService.saveActor(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateActor(@PathVariable Integer id, @Valid @RequestBody UpdateActorRequest request) {
        return ResponseEntity.ok(actorService.updateActor(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActor(@PathVariable Integer id) {
        actorService.deleteActor(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/update-avatar")
    public ResponseEntity<?> updateAvatar(@RequestParam("file") MultipartFile file, @PathVariable Integer id) {
        return ResponseEntity.ok(actorService.updateAvatar(id, file));
    }
}
