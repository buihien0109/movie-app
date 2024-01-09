package com.example.movieapp.repository;

import com.example.movieapp.entity.Episode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EpisodeRepository extends JpaRepository<Episode, Integer> {
    List<Episode> findAllByFilm_Id(Integer filmId);

    List<Episode> findByFilm_IdAndStatusOrderByDisplayOrderAsc(Integer id, Boolean status);

    Optional<Episode> findByFilm_IdAndStatusAndDisplayOrder(Integer film_id, Boolean status, Integer displayOrder);
}