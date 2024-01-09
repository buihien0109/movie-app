package com.example.movieapp.service;

import com.example.movieapp.entity.Genre;
import com.example.movieapp.exception.BadRequestException;
import com.example.movieapp.exception.ResouceNotFoundException;
import com.example.movieapp.model.request.UpsertGenreRequest;
import com.example.movieapp.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    public Page<Genre> getAllGenres(Integer page, Integer size) {
        return genreRepository.findAll(PageRequest.of(page, size));
    }

    public Genre getGenreById(Integer id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new ResouceNotFoundException("Không tìm thấy thể loại có id = " + id));
    }

    public Genre saveGenre(UpsertGenreRequest request) {
        // check tag name is exist
        if (genreRepository.findByName(request.getName()).isPresent()) {
            throw new BadRequestException("Thể loại đã tồn tại");
        }

        Genre genre = new Genre();
        genre.setName(request.getName());
        genreRepository.save(genre);
        return genre;
    }

    public Genre updateGenre(Integer id, UpsertGenreRequest genre) {
        Genre existingGenre = genreRepository.findById(id)
                .orElseThrow(() -> new ResouceNotFoundException("Không tìm thấy thể loại có id = " + id));

        existingGenre.setName(genre.getName());
        return genreRepository.save(existingGenre);
    }

    public void deleteGenre(Integer id) {
        Genre existingGenre = genreRepository.findById(id)
                .orElseThrow(() -> new ResouceNotFoundException("Không tìm thấy thể loại có id = " + id));
        genreRepository.deleteById(id);
    }
}
