package com.example.movieapp.repository;

import com.example.movieapp.entity.Film;
import com.example.movieapp.model.enums.FilmType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FilmRepository extends JpaRepository<Film, Integer> {
    Page<Film> findByTypeAndStatus(FilmType type, Boolean status, Pageable pageable);

    Optional<Film> findByIdAndSlug(Integer id, String slug);

    @Query(value = "SELECT * FROM films WHERE status = 1 ORDER BY view DESC LIMIT ?1", nativeQuery = true)
    List<Film> findPhimHot(Integer limit);
}