package com.example.movieapp.repository;

import com.example.movieapp.entity.Wishlist;
import com.example.movieapp.model.dto.WishlistDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    @Query("SELECT new com.example.movieapp.model.dto.WishlistDto(w.id, new com.example.movieapp.model.dto.FilmDto(f.id, f.title, f.slug, f.poster, f.type, f.accessType, f.rating, f.price, f.status, f.trailerUrl)) FROM Wishlist w JOIN w.film f WHERE w.user.id = ?1 ORDER BY w.createdAt DESC")
    List<WishlistDto> findByUser_Id(Integer userId);

    Optional<Wishlist> findByUser_IdAndFilm_Id(Integer userId, Integer filmId);

    boolean existsByUser_IdAndFilm_Id(Integer userId, Integer filmId);
}