package com.example.movieapp.service.impl;

import com.example.movieapp.dao.ActorDAO;
import com.example.movieapp.model.Actor;
import com.example.movieapp.model.Film;
import com.example.movieapp.response.PageResponse;
import com.example.movieapp.service.ActorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActorServiceImpl implements ActorService {
    private final ActorDAO actorDAO;

    @Override
    public PageResponse<Film> getAllActors(int page, int size) {
        return null;
    }

    @Override
    public void saveActor(Actor actor, MultipartFile file) {

    }

    @Override
    public Optional<Actor> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public void updateActor(Actor actor, MultipartFile file) {

    }

    @Override
    public void deleteActor(Integer id) {

    }
}
