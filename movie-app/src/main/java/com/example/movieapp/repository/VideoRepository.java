package com.example.movieapp.repository;

import com.example.movieapp.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Integer> {
}