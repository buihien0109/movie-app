package com.example.movieapp.repository;

import com.example.movieapp.entity.WatchHistory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Integer> {
    List<WatchHistory> findByUser_Id(Integer id, Sort sort);

    Optional<WatchHistory> findByUser_IdAndFilm_IdAndEpisode_Id(Integer userId, Integer filmId, Integer episodeId);

    Optional<WatchHistory> findByUser_IdAndFilm_Id(Integer userId, Integer filmId);

    void deleteAllByUser_Id(Integer id);
}