package com.example.movieapp.repository;

import com.example.movieapp.entity.Film;
import com.example.movieapp.model.dto.FilmDetailDto;
import com.example.movieapp.model.dto.FilmDto;
import com.example.movieapp.model.dto.FilmViewDto;
import com.example.movieapp.model.enums.FilmAccessType;
import com.example.movieapp.model.enums.FilmType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.net.ContentHandler;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface FilmRepository extends JpaRepository<Film, Integer> {
    // Tìm kiếm phim related -> lấy danh sách phim theo type, accessType, status nhưng không chưa phim hiện tại -> sắp xếp theo publishedAt giảm dần -> phân trang -> return Page<FilmDto>
    @Query("SELECT new com.example.movieapp.model.dto.FilmDto(f.id, f.title, f.slug, f.poster, f.type, f.accessType, f.rating, f.price, f.status, f.trailerUrl) FROM Film f WHERE f.type = ?1 AND f.accessType = ?2 AND f.status = ?3 AND f.id <> ?4 ORDER BY f.publishedAt DESC")
    Page<FilmDto> findRelateFilms(FilmType type, FilmAccessType accessType, Boolean status, Integer filmId, Pageable pageable);

    // Tìm kiếm phim related -> lấy danh sách phim theo type, accessType, status nhưng không chưa phim hiện tại -> sắp xếp theo publishedAt giảm dần -> phân trang -> return Page<Film>
    @Query("SELECT f FROM Film f WHERE f.type = ?1 AND f.accessType = ?2 AND f.status = ?3 AND f.id <> ?4 ORDER BY f.publishedAt DESC")
    Page<Film> findRelateProFilms(FilmType type, FilmAccessType accessType, Boolean status, Integer filmId, Pageable pageable);

    // Tìm kiếm phim theo type, accessType, status -> sắp xếp theo publishedAt giảm dần -> phân trang -> return Page<FilmDto>
    @Query("SELECT new com.example.movieapp.model.dto.FilmDto(f.id, f.title, f.slug, f.poster, f.type, f.accessType, f.rating, f.price, f.status, f.trailerUrl) FROM Film f WHERE f.type = ?1 AND f.accessType = ?2 AND f.status = ?3")
    Page<FilmDto> findByTypeAndAccessTypeAndStatus(FilmType type, FilmAccessType accessType, Boolean status, Pageable pageable);

    // Lấy danh sách phim hot -> sắp xếp theo số lượt xem giảm dần -> giới hạn số lượng phim trả về -> return List<FilmDto>
    @Query(value = "SELECT new com.example.movieapp.model.dto.FilmDto(f.id, f.title, f.slug, f.poster, f.type, f.accessType, f.rating, f.price, f.status, f.trailerUrl) FROM Film f WHERE f.status = true AND f.accessType = 'FREE' ORDER BY f.view DESC")
    List<FilmDto> findPhimHot();

    long countByGenres_Id(Integer id);

    long countByDirectors_Id(Integer id);

    long countByActors_Id(Integer id);

    @Modifying
    @Query(value = "update films set view = view + 1 where id = ?1", nativeQuery = true)
    void updateView(Integer id);

    @Query("SELECT new com.example.movieapp.model.dto.FilmDto(f.id, f.title, f.slug, f.poster, f.type, f.accessType, f.rating, f.price, f.status, f.trailerUrl) FROM Film f WHERE f.accessType = ?1 AND f.status = ?2 ORDER BY f.publishedAt DESC")
    Page<FilmDto> findByAccessTypeAndStatus(FilmAccessType accessType, Boolean status, Pageable pageable);

    List<Film> findByAccessTypeAndStatus(FilmAccessType accessType, Boolean status);

    // Get FilmDetailDto by film id, film slug, film access type -> return Optional<FilmDetailDto>. In FilmDetailDto has CountryDto, Set<GenreDto>, Set<ActorDto>, Set<DirectorDto> using JPQL
//    @Query("SELECT new com.example.movieapp.model.dto.FilmDetailDto(f.id, f.title, f.slug, f.description, f.poster, f.releaseYear, f.view, f.rating, f.type, f.accessType, f.price, f.status, f.trailerUrl, f.createdAt, f.updatedAt, f.publishedAt, new com.example.movieapp.model.dto.CountryDto(f.country.id, f.country.name, f.country.slug), g, a, d) FROM Film f JOIN f.genres g JOIN f.actors a JOIN f.directors d WHERE f.id = ?1 AND f.slug = ?2 AND f.accessType = ?3")
    Optional<Film> findByIdAndSlugAndAccessType(Integer id, String slug, FilmAccessType accessType);

    List<Film> findByIdBetween(Integer start, Integer end);

    // join films and orders and find films buyed of user and status of order is success and return List<FilmDto> using jqpl query
    @Query("SELECT new com.example.movieapp.model.dto.FilmDto(f.id, f.title, f.slug, f.poster, f.type, f.accessType, f.rating, f.price, f.status, f.trailerUrl) FROM Order o JOIN o.film f WHERE o.user.id = ?1 AND o.status = 'SUCCESS' ORDER BY o.createdAt DESC")
    List<FilmDto> findFilmsBuyedOfUser(Integer id);

    long countByCreatedAtBetween(Date start, Date end);

    // find top 10 films view in month, join films and view_film_logs and group by film_id and order by count view desc using jpql query and return type is FilmViewDto
    @Query(value = "SELECT new com.example.movieapp.model.dto.FilmViewDto(f.id, f.title, f.slug, COUNT(v.id)) FROM Film f JOIN ViewFilmLog v ON f.id = v.film.id WHERE v.viewTime BETWEEN ?1 AND ?2 GROUP BY f.id ORDER BY COUNT(v.id) DESC")
    List<FilmViewDto> findTopViewFilms(Date start, Date end);

    Optional<Film> findByIdAndAccessType(Integer id, FilmAccessType filmAccessType);

    List<Film> findByAccessType(FilmAccessType filmAccessType);

    long countByCountry_Id(Integer id);

    @Query("SELECT new com.example.movieapp.model.dto.FilmDto(f.id, f.title, f.slug, f.poster, f.type, f.accessType, f.rating, f.price, f.status, f.trailerUrl) FROM Film f JOIN f.country c WHERE f.accessType = ?1 AND f.status = ?2 AND c.slug = ?3 ORDER BY f.publishedAt DESC")
    Page<FilmDto> findByAccessTypeAndStatusAndCountry_SlugOrderByPublishedAtDesc(FilmAccessType accessType, Boolean status, String countrySlug, Pageable pageable);

    @Query("SELECT new com.example.movieapp.model.dto.FilmDto(f.id, f.title, f.slug, f.poster, f.type, f.accessType, f.rating, f.price, f.status, f.trailerUrl) FROM Film f JOIN f.genres g WHERE f.accessType = ?1 AND f.status = ?2 AND g.slug = ?3 ORDER BY f.publishedAt DESC")
    Page<FilmDto> findByAccessTypeAndStatusAndGenres_SlugOrderByPublishedAtDesc(FilmAccessType accessType, Boolean status, String genreSlug, Pageable pageable);
}