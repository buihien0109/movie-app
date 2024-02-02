package com.example.movieapp.repository;

import com.example.movieapp.entity.Blog;
import com.example.movieapp.model.dto.BlogDetailDto;
import com.example.movieapp.model.dto.BlogDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Integer> {
    @Query("SELECT new com.example.movieapp.model.dto.BlogDto(b.id, b.title, b.slug, b.description, b.thumbnail, b.publishedAt) FROM Blog b WHERE b.status = ?1")
    Page<BlogDto> findByStatus(Boolean status, Pageable pageable);

    Page<Blog> findByUser_Id(Integer id, Pageable pageable);

    @Query("SELECT new com.example.movieapp.model.dto.BlogDetailDto(b.id, b.title, b.slug, b.description, b.content, b.thumbnail, b.createdAt, b.updatedAt, b.publishedAt, b.status, new com.example.movieapp.model.dto.UserDto(b.user.id, b.user.name, b.user.email, b.user.phone, b.user.avatar, b.user.role, b.user.enabled)) FROM Blog b WHERE b.id = ?1 AND b.slug = ?2 AND b.status = ?3")
    Optional<BlogDetailDto> findByIdAndSlugAndStatus(Integer id, String slug, boolean b);

    long countByCreatedAtBetween(Date start, Date end);

    @Query("SELECT new com.example.movieapp.model.dto.BlogDto(b.id, b.title, b.slug, b.description, b.thumbnail, b.publishedAt) FROM Blog b WHERE b.id <> ?1 AND b.status = ?2")
    Page<BlogDto> findByIdNotAndStatus(Integer blogId, Boolean status, Pageable pageable);
}