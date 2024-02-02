package com.example.movieapp.repository;

import com.example.movieapp.entity.Episode;
import com.example.movieapp.model.dto.EpisodeDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EpisodeRepository extends JpaRepository<Episode, Integer> {
    List<Episode> findAllByFilm_Id(Integer filmId);

    @Query("SELECT new com.example.movieapp.model.dto.EpisodeDto(e.id, e.displayOrder, e.title, e.status, e.video.duration, e.video.url) FROM Episode e WHERE e.film.id = ?1 AND e.status = ?2 ORDER BY e.displayOrder ASC")
    List<EpisodeDto> findByFilm_IdAndStatusOrderByDisplayOrderAsc(Integer id, Boolean status);

    @Query("SELECT new com.example.movieapp.model.dto.EpisodeDto(e.id, e.displayOrder, e.title, e.status, e.video.duration, e.video.url) FROM Episode e WHERE e.film.id = ?1 AND e.status = ?2 AND e.displayOrder = ?3")
    Optional<EpisodeDto> findByFilm_IdAndStatusAndDisplayOrder(Integer film_id, Boolean status, Integer displayOrder);
}