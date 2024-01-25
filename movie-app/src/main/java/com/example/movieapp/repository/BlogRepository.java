package com.example.movieapp.repository;

import com.example.movieapp.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Integer> {
    Page<Blog> findByStatus(Boolean status, Pageable pageable);

    Page<Blog> findByUser_Id(Integer id, Pageable pageable);

    Optional<Blog> findByIdAndSlugAndStatus(Integer id, String slug, boolean b);
}