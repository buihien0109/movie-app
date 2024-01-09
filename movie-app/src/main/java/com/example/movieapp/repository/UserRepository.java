package com.example.movieapp.repository;

import com.example.movieapp.entity.User;
import com.example.movieapp.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    List<User> findByRole(UserRole role);
}