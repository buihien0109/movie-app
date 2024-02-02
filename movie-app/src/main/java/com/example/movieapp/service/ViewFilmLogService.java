package com.example.movieapp.service;

import com.example.movieapp.entity.Film;
import com.example.movieapp.entity.ViewFilmLog;
import com.example.movieapp.exception.ResourceNotFoundException;
import com.example.movieapp.repository.FilmRepository;
import com.example.movieapp.repository.ViewFilmLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViewFilmLogService {
    private final ViewFilmLogRepository viewFilmLogRepository;
    private final FilmRepository filmRepository;

    @Transactional
    public void createViewFilmLog(Integer filmId) {
        // Kiểm tra phim có tồn tại không? Nếu không thì báo lỗi.
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim có id = " + filmId));

        // Lưu thông tin vào bảng view_film_logs.
        ViewFilmLog viewFilmLog = ViewFilmLog.builder()
                .film(film)
                .build();

        viewFilmLogRepository.save(viewFilmLog);

        // Cập nhật số lượt xem của phim.
        filmRepository.updateView(filmId);
    }
}
