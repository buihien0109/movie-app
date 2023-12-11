package com.example.movieapp.service;

import com.example.movieapp.model.Actor;
import com.example.movieapp.model.Film;
import com.example.movieapp.response.PageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ActorService {
    PageResponse<Film> getAllActors(int page, int size);

    void saveActor(Actor actor, MultipartFile file);

    Optional<Actor> findById(Integer id);

    void updateActor(Actor actor, MultipartFile file);

    void deleteActor(Integer id);
}
