package com.example.movieapp.repository;

import com.example.movieapp.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    List<Wishlist> findByUser_Id(Integer userId);

    Optional<Wishlist> findByUser_IdAndFilm_Id(Integer userId, Integer filmId);

    boolean existsByUser_IdAndFilm_Id(Integer userId, Integer filmId);
}