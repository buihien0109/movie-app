package com.example.movieapp.service;

import com.example.movieapp.entity.Film;
import com.example.movieapp.entity.Genre;
import com.example.movieapp.exception.BadRequestException;
import com.example.movieapp.exception.ResourceNotFoundException;
import com.example.movieapp.model.enums.FilmAccessType;
import com.example.movieapp.model.request.UpsertGenreRequest;
import com.example.movieapp.repository.FilmRepository;
import com.example.movieapp.repository.GenreRepository;
import com.github.slugify.Slugify;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;
    private final FilmRepository filmRepository;
    private final Slugify slugify;

    public List<Genre> getAllGenres() {
        return genreRepository.findAll(Sort.by("createdAt").descending());
    }

    public Page<Genre> getAllGenres(Integer page, Integer size) {
        return genreRepository.findAll(PageRequest.of(page, size));
    }

    public Genre getGenreById(Integer id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại có id = " + id));
    }

    public Genre saveGenre(UpsertGenreRequest request) {
        // check tag name is exist
        if (genreRepository.findByName(request.getName()).isPresent()) {
            throw new BadRequestException("Thể loại đã tồn tại");
        }

        Genre genre = new Genre();
        genre.setName(request.getName());
        genre.setSlug(slugify.slugify(request.getName()));
        genreRepository.save(genre);
        return genre;
    }

    public Genre updateGenre(Integer id, UpsertGenreRequest genre) {
        Genre existingGenre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại có id = " + id));

        // Kiểm tra tên thể loại đã tồn tại hay chưa. Nếu đã tồn tại và không phải là thể loại cần update thì throw exception
        if (genreRepository.findByName(genre.getName()).isPresent() && !Objects.equals(existingGenre.getName(), genre.getName())) {
            throw new BadRequestException("Thể loại đã tồn tại");
        }

        existingGenre.setName(genre.getName());
        existingGenre.setSlug(slugify.slugify(genre.getName()));
        return genreRepository.save(existingGenre);
    }

    public void deleteGenre(Integer id) {
        Genre existingGenre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại có id = " + id));

        // Đếm số phim có thể loại này
        long count = filmRepository.countByGenres_Id(existingGenre.getId());
        if (count > 0) {
            throw new BadRequestException("Không thể xóa thể loại này vì có " + count + " phim thuộc thể loại này");
        }

        genreRepository.deleteById(id);
    }

    public Genre getGenreBySlug(String slug) {
        return genreRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại có slug = " + slug));
    }
}
