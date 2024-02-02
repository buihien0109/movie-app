package com.example.movieapp.repository;

import com.example.movieapp.entity.TokenConfirm;
import com.example.movieapp.model.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenConfirmRepository extends JpaRepository<TokenConfirm, Integer> {
    Optional<TokenConfirm> findByTokenAndType(String token, TokenType tokenType);
}