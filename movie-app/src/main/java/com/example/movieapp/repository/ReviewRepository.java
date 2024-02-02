package com.example.movieapp.repository;

import com.example.movieapp.entity.Review;
import com.example.movieapp.model.dto.ReviewDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    // Get all reviews of a film by film id and order by created date descending (newest first) -> return list of ReviewDto. In reviewDto has UserDto -> using JQPL query
    @Query("SELECT new com.example.movieapp.model.dto.ReviewDto(r.id, r.rating, r.comment, r.createdAt, r.updatedAt, new com.example.movieapp.model.dto.UserDto(r.user.id, r.user.name, r.user.email, r.user.phone, r.user.avatar, r.user.role, r.user.enabled)) FROM Review r WHERE r.film.id = ?1 ORDER BY r.createdAt DESC")
    List<ReviewDto> findByFilm_IdOrderByCreatedAtDesc(Integer id);
}