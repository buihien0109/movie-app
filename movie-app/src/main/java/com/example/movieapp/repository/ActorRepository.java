package com.example.movieapp.repository;

import com.example.movieapp.entity.Actor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.nio.CharBuffer;
import java.util.Optional;
import java.util.Set;

public interface ActorRepository extends JpaRepository<Actor, Integer> {
    Set<Actor> findByIdIn(Set<Integer> actorIds);

    boolean existsByName(String name);

    Optional<Actor> findByName(String name);
}