package com.example.movieapp.repository;

import com.example.movieapp.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface GenreRepository extends JpaRepository<Genre, Integer> {
    Optional<Genre> findByName(String name);

    Set<Genre> findByIdIn(Set<Integer> genreIds);

    boolean existsByName(String genre);

    Optional<Genre> findBySlug(String slug);
}