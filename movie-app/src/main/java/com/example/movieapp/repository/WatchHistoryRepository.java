package com.example.movieapp.repository;

import com.example.movieapp.entity.WatchHistory;
import com.example.movieapp.model.dto.WatchHistoryDto;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Integer> {
    // Get all WatchHistory of user by user id and sort by watchTime descending -> return List<WatchHistory>. In WatchHistory has FilmDto, UserDto, EpisodeDto using JPQL
    @Query("SELECT new com.example.movieapp.model.dto.WatchHistoryDto(w.id, w.watchTime, w.duration, new com.example.movieapp.model.dto.UserDto(w.user.id, w.user.name, w.user.email, w.user.phone, w.user.avatar, w.user.role, w.user.enabled), new com.example.movieapp.model.dto.FilmDto(w.film.id, w.film.title, w.film.slug, w.film.poster, w.film.type, w.film.accessType, w.film.rating, w.film.price, w.film.status, w.film.trailerUrl), new com.example.movieapp.model.dto.EpisodeDto(w.episode.id, w.episode.displayOrder, w.episode.title, w.episode.status, w.episode.video.duration, w.episode.video.url)) FROM WatchHistory w WHERE w.user.id = ?1")
    List<WatchHistoryDto> findByUser_Id(Integer id, Sort sort);

    @Query("SELECT new com.example.movieapp.model.dto.WatchHistoryDto(w.id, w.watchTime, w.duration, new com.example.movieapp.model.dto.UserDto(w.user.id, w.user.name, w.user.email, w.user.phone, w.user.avatar, w.user.role, w.user.enabled), new com.example.movieapp.model.dto.FilmDto(w.film.id, w.film.title, w.film.slug, w.film.poster, w.film.type, w.film.accessType, w.film.rating, w.film.price, w.film.status, w.film.trailerUrl), new com.example.movieapp.model.dto.EpisodeDto(w.episode.id, w.episode.displayOrder, w.episode.title, w.episode.status, w.episode.video.duration, w.episode.video.url)) FROM WatchHistory w WHERE w.user.id = ?1 AND w.film.id = ?2 AND w.episode.id = ?3")
    Optional<WatchHistoryDto> findByUser_IdAndFilm_IdAndEpisode_Id(Integer userId, Integer filmId, Integer episodeId);

    Optional<WatchHistory> findByUser_IdAndFilm_Id(Integer userId, Integer filmId);

    void deleteAllByUser_Id(Integer id);
}