package com.example.movieapp.repository;

import com.example.movieapp.entity.ViewFilmLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewFilmLogRepository extends JpaRepository<ViewFilmLog, Integer> {
}