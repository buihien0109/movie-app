package com.example.movieapp.service;

import com.example.movieapp.entity.Actor;
import com.example.movieapp.entity.Director;
import com.example.movieapp.entity.Film;
import com.example.movieapp.entity.Genre;
import com.example.movieapp.exception.ResouceNotFoundException;
import com.example.movieapp.model.request.CreateFilmRequest;
import com.example.movieapp.model.request.UpdateFilmRequest;
import com.example.movieapp.repository.ActorRepository;
import com.example.movieapp.repository.DirectorRepository;
import com.example.movieapp.repository.FilmRepository;
import com.example.movieapp.repository.GenreRepository;
import com.example.movieapp.utils.StringUtils;
import com.github.slugify.Slugify;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    public List<Film> getAllFilms() {
        return filmRepository.findAll();
    }

    public Page<Film> getAllFilms(Integer page, Integer size) {
        return filmRepository.findAll(PageRequest.of(page, size));
    }

    public Film getFilmById(Integer id) {
        return filmRepository.findById(id)
                .orElseThrow(() -> new ResouceNotFoundException("Không tìm thấy phim có id = " + id));
    }

    public Film saveFilm(CreateFilmRequest request) {
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
                .genres(genres)
                .directors(directors)
                .actors(actors)
                .build();
        return filmRepository.save(film);
    }

    public Film updateFilm(Integer id, UpdateFilmRequest request) {
        Film existingFilm = filmRepository.findById(id)
                .orElseThrow(() -> new ResouceNotFoundException("Không tìm thấy phim có id = " + id));

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
        existingFilm.setGenres(genres);
        existingFilm.setDirectors(directors);
        existingFilm.setActors(actors);
        return filmRepository.save(existingFilm);
    }

    public void deleteFilm(Integer id) {
        Film existingFilm = filmRepository.findById(id)
                .orElseThrow(() -> new ResouceNotFoundException("Không tìm thấy phim có id = " + id));
        filmRepository.deleteById(id);
    }

    public String updatePoster(Integer id, MultipartFile file) {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new ResouceNotFoundException("Không tìm thấy phim"));

        String filePath = fileService.saveFile(file);
        film.setPoster(filePath);
        filmRepository.save(film);
        return film.getPoster();
    }
}
