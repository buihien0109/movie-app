package com.example.movieapp.repository;

import com.example.movieapp.entity.Film;
import com.example.movieapp.model.dto.FilmViewDto;
import com.example.movieapp.model.enums.FilmAccessType;
import com.example.movieapp.model.enums.FilmType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface FilmRepository extends JpaRepository<Film, Integer> {
    Page<Film> findByTypeAndStatus(FilmType type, Boolean status, Pageable pageable);

    Page<Film> findByTypeAndAccessTypeAndStatus(FilmType type, FilmAccessType accessType, Boolean status, Pageable pageable);

    Optional<Film> findByIdAndSlug(Integer id, String slug);

    @Query(value = "SELECT * FROM films WHERE status = 1 AND access_type = 'FREE' ORDER BY view DESC LIMIT ?1", nativeQuery = true)
    List<Film> findPhimHot(Integer limit);

    long countByGenres_Id(Integer id);

    long countByDirectors_Id(Integer id);

    long countByActors_Id(Integer id);

    @Modifying
    @Query(value = "update films set view = view + 1 where id = ?1", nativeQuery = true)
    void updateView(Integer id);

    Page<Film> findByAccessTypeAndStatus(FilmAccessType accessType, Boolean status, Pageable pageable);

    List<Film> findByAccessTypeAndStatus(FilmAccessType accessType, Boolean status);

    Optional<Film> findByIdAndSlugAndAccessType(Integer id, String slug, FilmAccessType accessType);

    List<Film> findByIdBetween(Integer start, Integer end);

    // join films and orders and find films buyed of user and status of order is success
    @Query(value = "SELECT f.* FROM films f JOIN orders o ON f.id = o.film_id WHERE o.user_id = ?1 AND o.status = 'SUCCESS'", nativeQuery = true)
    List<Film> findFilmsBuyedOfUser(Integer id);

    List<Film> findAllByCreatedAtBetweenOrderByCreatedAtDesc(Date start, Date end);

    long countByCreatedAtBetween(Date start, Date end);

    // find top 10 films view in month, join films and view_film_logs and group by film_id and order by count view desc using jpql query and return type is FilmViewDto
    @Query(value = "SELECT new com.example.movieapp.model.dto.FilmViewDto(f.id, f.title, f.slug, COUNT(v.id)) FROM Film f JOIN ViewFilmLog v ON f.id = v.film.id WHERE v.viewTime BETWEEN ?1 AND ?2 GROUP BY f.id ORDER BY COUNT(v.id) DESC")
    List<FilmViewDto> findTopViewFilms(Date start, Date end);

    Optional<Film> findByIdAndAccessType(Integer id, FilmAccessType filmAccessType);

    List<Film> findByAccessType(FilmAccessType filmAccessType);

    long countByCountry_Id(Integer id);

    Page<Film> findByAccessTypeAndStatusAndCountry_SlugOrderByPublishedAtDesc(FilmAccessType accessType, Boolean status, String countrySlug, Pageable pageable);

    Page<Film> findByAccessTypeAndStatusAndGenres_SlugOrderByPublishedAtDesc(FilmAccessType accessType, Boolean status, String genreSlug, Pageable pageable);
}