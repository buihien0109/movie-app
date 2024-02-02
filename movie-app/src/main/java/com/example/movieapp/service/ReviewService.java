package com.example.movieapp.service;

import com.example.movieapp.entity.Film;
import com.example.movieapp.entity.Review;
import com.example.movieapp.entity.User;
import com.example.movieapp.exception.BadRequestException;
import com.example.movieapp.exception.ResourceNotFoundException;
import com.example.movieapp.model.dto.ReviewDto;
import com.example.movieapp.model.request.UpsertReviewRequest;
import com.example.movieapp.repository.FilmRepository;
import com.example.movieapp.repository.ReviewRepository;
import com.example.movieapp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final FilmRepository filmRepository;
    private final ReviewRepository reviewRepository;

    public List<ReviewDto> getReviewsOfFilm(Integer filmId) {
        return reviewRepository.findByFilm_IdOrderByCreatedAtDesc(filmId);
    }

    public Review createReview(UpsertReviewRequest request) {
        // get user from spring security
        User user = SecurityUtils.getCurrentUserLogin();

        // find film and check film exist
        Film film = filmRepository.findById(request.getFilmId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim có id = " + request.getFilmId()));

        Review review = Review.builder()
                .user(user)
                .comment(request.getComment())
                .rating(request.getRating())
                .film(film)
                .build();
        return reviewRepository.save(review);
    }

    public Review updateReview(UpsertReviewRequest request, Integer id) {
        // get user from session
        User user = SecurityUtils.getCurrentUserLogin();

        // find film and check film exist
        Film film = filmRepository.findById(request.getFilmId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim có id = " + request.getFilmId()));

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy review có id = " + id));

        // check user is owner of review
        if (!review.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Bạn không có quyền sửa review này");
        }

        review.setComment(request.getComment());
        review.setRating(request.getRating());
        review.setFilm(film);

        return reviewRepository.save(review);
    }

    public void deleteReview(Integer id) {
        // get user from session
        User user = SecurityUtils.getCurrentUserLogin();

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy review có id = " + id));

        // check user is owner of review
        if (!review.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Bạn không có quyền xóa review này");
        }

        reviewRepository.delete(review);
    }

    public void adminDeleteReview(Integer id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy review có id = " + id));
        reviewRepository.delete(review);
    }
}
