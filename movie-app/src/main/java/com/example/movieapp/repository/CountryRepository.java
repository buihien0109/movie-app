package com.example.movieapp.repository;

import com.example.movieapp.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Integer> {
    Optional<Country> findByName(String name);

    Optional<Country> findBySlug(String slug);
}