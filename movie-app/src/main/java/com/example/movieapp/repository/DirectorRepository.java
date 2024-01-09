package com.example.movieapp.repository;

import com.example.movieapp.entity.Director;
import org.springframework.data.jpa.repository.JpaRepository;

import java.nio.CharBuffer;
import java.util.Optional;
import java.util.Set;

public interface DirectorRepository extends JpaRepository<Director, Integer> {
    Set<Director> findByIdIn(Set<Integer> directorIds);

    boolean existsByName(String name);

    Optional<Director> findByName(String name);
}