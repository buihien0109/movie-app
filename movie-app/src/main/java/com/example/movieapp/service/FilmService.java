package com.example.movieapp.service;

import com.example.movieapp.entity.*;
import com.example.movieapp.exception.ResourceNotFoundException;
import com.example.movieapp.model.dto.FilmDto;
import com.example.movieapp.model.enums.FilmAccessType;
import com.example.movieapp.model.request.CreateFilmRequest;
import com.example.movieapp.model.request.UpdateFilmRequest;
import com.example.movieapp.repository.*;
import com.example.movieapp.security.SecurityUtils;
import com.example.movieapp.utils.FileUtils;
import com.example.movieapp.utils.StringUtils;
import com.github.slugify.Slugify;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmRepository filmRepository;
    private final FileService fileService;
    private final GenreRepository genreRepository;
    private final ActorRepository actorRepository;
    private final DirectorRepository directorRepository;
    private final Slugify slugify;
    private final CountryRepository countryRepository;

    public List<Film> getAllFilms() {
        return filmRepository.findAll();
    }

    public Page<Film> getAllFilms(Integer page, Integer size) {
        return filmRepository.findAll(PageRequest.of(page, size));
    }

    public Film getFilmById(Integer id) {
        return filmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim có id = " + id));
    }

    public Film saveFilm(CreateFilmRequest request) {
        Country country = countryRepository.findById(request.getCountryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy quốc gia có id = " + request.getCountryId()));

        // get all genres by genreIds
        Set<Genre> genres = genreRepository.findByIdIn(request.getGenreIds());

        // get all directors by directorIds
        Set<Director> directors = directorRepository.findByIdIn(request.getDirectorIds());

        // get all actors by actorIds
        Set<Actor> actors = actorRepository.findByIdIn(request.getActorIds());

        // create film
        Film film = Film.builder()
                .title(request.getTitle())
                .slug(slugify.slugify(request.getTitle()))
                .description(request.getDescription())
                .releaseYear(request.getReleaseYear())
                .poster(StringUtils.generateLinkImage(request.getTitle()))
                .type(request.getType())
                .status(request.getStatus())
                .country(country)
                .accessType(request.getAccessType())
                .price(request.getPrice())
                .genres(genres)
                .directors(directors)
                .actors(actors)
                .build();
        return filmRepository.save(film);
    }

    public Film updateFilm(Integer id, UpdateFilmRequest request) {
        Film existingFilm = filmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim có id = " + id));

        Country country = countryRepository.findById(request.getCountryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy quốc gia có id = " + request.getCountryId()));

        // get all genres by genreIds
        Set<Genre> genres = genreRepository.findByIdIn(request.getGenreIds());

        // get all directors by directorIds
        Set<Director> directors = directorRepository.findByIdIn(request.getDirectorIds());

        // get all actors by actorIds
        Set<Actor> actors = actorRepository.findByIdIn(request.getActorIds());

        // update film
        existingFilm.setTitle(request.getTitle());
        existingFilm.setSlug(slugify.slugify(request.getTitle()));
        existingFilm.setDescription(request.getDescription());
        existingFilm.setReleaseYear(request.getReleaseYear());
        existingFilm.setType(request.getType());
        existingFilm.setStatus(request.getStatus());
        existingFilm.setCountry(country);
        existingFilm.setAccessType(request.getAccessType());
        existingFilm.setPrice(request.getPrice());
        existingFilm.setGenres(genres);
        existingFilm.setDirectors(directors);
        existingFilm.setActors(actors);
        return filmRepository.save(existingFilm);
    }

    public void deleteFilm(Integer id) {
        Film existingFilm = filmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim có id = " + id));

        // Kiểm tra xem phim có poster không. Nếu có thì xóa file poster
        if (existingFilm.getPoster() != null) {
            FileUtils.deleteFile(existingFilm.getPoster());
        }

        filmRepository.deleteById(id);
    }

    public String updatePoster(Integer id, MultipartFile file) {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim"));

        // Kiểm tra xem phim có poster không. Nếu có thì xóa file poster sau đó lưu file mới
        if (film.getPoster() != null) {
            FileUtils.deleteFile(film.getPoster());
        }

        String filePath = fileService.saveFile(file);
        film.setPoster(filePath);
        filmRepository.save(film);
        return film.getPoster();
    }

    public List<FilmDto> getFilmsBuyedOfCurrentUser() {
        User user = SecurityUtils.getCurrentUserLogin();
        return filmRepository.findFilmsBuyedOfUser(user.getId());
    }

    public List<Film> getAllFilmsByAccessType(FilmAccessType filmAccessType) {
        return filmRepository.findByAccessType(filmAccessType);
    }

    public Page<FilmDto> getFilmsOfGenre(String slug, FilmAccessType filmAccessType, boolean status, Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        return filmRepository.findByAccessTypeAndStatusAndGenres_SlugOrderByPublishedAtDesc(filmAccessType, status, slug, pageable);
    }

    public Page<FilmDto> getFilmsOfCountry(String slug, FilmAccessType filmAccessType, boolean status, Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        return filmRepository.findByAccessTypeAndStatusAndCountry_SlugOrderByPublishedAtDesc(filmAccessType, status, slug, pageable);
    }
}
