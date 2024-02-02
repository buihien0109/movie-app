package com.example.movieapp.service;

import com.example.movieapp.entity.Episode;
import com.example.movieapp.entity.Film;
import com.example.movieapp.exception.ResourceNotFoundException;
import com.example.movieapp.model.request.UpsertEpisodeRequest;
import com.example.movieapp.repository.EpisodeRepository;
import com.example.movieapp.repository.FilmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EpisodeService {
    private final EpisodeRepository episodeRepository;
    private final VideoService videoService;
    private final FilmRepository filmRepository;

    public List<Episode> getAllEpisodesOfFilm(Integer filmId) {
        return episodeRepository.findAllByFilm_Id(filmId);
    }

    public Episode saveEpisode(UpsertEpisodeRequest request) {
        // Check film is exist
        Film film = filmRepository.findById(request.getFilmId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim có id = " + request.getFilmId()));

        Episode episode = Episode.builder()
                .title(request.getTitle())
                .displayOrder(request.getDisplayOrder())
                .status(request.getStatus())
                .film(film)
                .build();

        return episodeRepository.save(episode);
    }

    public Episode updateEpisode(Integer id, UpsertEpisodeRequest request) {
        Episode existingEpisode = episodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tập phim có id = " + id));

        // Check film is exist
        Film film = filmRepository.findById(request.getFilmId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim có id = " + request.getFilmId()));

        existingEpisode.setTitle(request.getTitle());
        existingEpisode.setDisplayOrder(request.getDisplayOrder());
        existingEpisode.setFilm(film);
        existingEpisode.setStatus(request.getStatus());
        return episodeRepository.save(existingEpisode);
    }

    public void deleteEpisode(Integer id) {
        Episode existingEpisode = episodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tập phim có id = " + id));
        episodeRepository.deleteById(id);

        // Delete video
        if (existingEpisode.getVideo() != null) {
            videoService.deleteVideo(existingEpisode.getVideo().getId());
        }
    }

    public Episode uploadVideo(Integer id, MultipartFile file) {
        Episode existingEpisode = episodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tập phim có id = " + id));

        // Delete old video
        if (existingEpisode.getVideo() != null) {
            videoService.deleteVideo(existingEpisode.getVideo().getId());
        }

        // Upload new video
        existingEpisode.setVideo(videoService.createVideo(file));
        return episodeRepository.save(existingEpisode);
    }
}
