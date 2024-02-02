package com.example.movieapp.service;

import com.example.movieapp.entity.Episode;
import com.example.movieapp.entity.Film;
import com.example.movieapp.entity.User;
import com.example.movieapp.entity.WatchHistory;
import com.example.movieapp.exception.BadRequestException;
import com.example.movieapp.exception.ResourceNotFoundException;
import com.example.movieapp.model.dto.WatchHistoryDto;
import com.example.movieapp.model.request.WatchHistoryRequest;
import com.example.movieapp.repository.EpisodeRepository;
import com.example.movieapp.repository.FilmRepository;
import com.example.movieapp.repository.WatchHistoryRepository;
import com.example.movieapp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WatchHistoryService {
    private final WatchHistoryRepository watchHistoryRepository;
    private final FilmRepository filmRepository;
    private final EpisodeRepository episodeRepository;

    public void saveWatchFilm(WatchHistoryRequest request) {
        log.info("Saving watch movie: {}", request);
        // Lấy thông tin user từ context
        User user = SecurityUtils.getCurrentUserLogin();

        // Kiểm tra xem phim có tồn tại không? Nếu không tồn tại thì throw exception
        Film film = filmRepository.findById(request.getFilmId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim với id: " + request.getFilmId()));

        // Kiểm tra xem tập phim có tồn tại không? Nếu không tồn tại thì throw exception
        Episode episode = episodeRepository.findById(request.getEpisodeId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tập phim với id: " + request.getEpisodeId()));

        // Kiểm tra xem thời lượng xem có hợp lệ không? Nếu không hợp lệ thì throw exception
        if (request.getDuration() < 0 || request.getDuration() > episode.getVideo().getDuration()) {
            throw new BadRequestException("Thời lượng xem không hợp lệ");
        }

        // Kiểm tra xem người dùng đã xem phim này chưa? Nếu đã xem rồi cập nhật lại thời lượng xem
        WatchHistory watchHistory = watchHistoryRepository.findByUser_IdAndFilm_Id(
                user.getId(),
                film.getId()
        ).orElse(null);

        if (watchHistory == null) {
            // Nếu chưa xem thì tạo mới lịch sử xem phim
            watchHistory = WatchHistory.builder()
                    .user(user)
                    .film(film)
                    .episode(episode)
                    .duration(request.getDuration())
                    .build();
        } else {
            // Nếu trùng episode thì cập nhật lại thời lượng xem
            if (watchHistory.getEpisode().getId().equals(episode.getId())) {
                watchHistory.setDuration(request.getDuration());
            } else {
                // Nếu không trùng episode thì cập nhật lại episode và thời lượng xem
                watchHistory.setEpisode(episode);
                watchHistory.setDuration(request.getDuration());
            }
        }

        // Lưu lịch sử xem phim
        watchHistoryRepository.save(watchHistory);
    }

    public List<WatchHistoryDto> getWatchHistoriesOfCurrentUser() {
        log.info("Getting watch histories of current user");
        User user = SecurityUtils.getCurrentUserLogin();

        return watchHistoryRepository.findByUser_Id(
                user.getId(),
                Sort.by(Sort.Direction.DESC, "watchTime"));
    }

    public void deleteWatchFilm(Integer id) {
        // Lấy thông tin user từ context
        User user = SecurityUtils.getCurrentUserLogin();

        // Kiểm tra lịch sử có tồn tại không? Nếu không tồn tại thì throw exception
        WatchHistory watchHistory = watchHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch sử xem phim với id: " + id));

        // Kiểm tra xem lịch sử xem phim này có phải của user hiện tại không? Nếu không phải thì throw exception
        if (!watchHistory.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Bạn không có quyền xóa lịch sử xem phim này");
        }

        // Xóa lịch sử xem phim
        watchHistoryRepository.delete(watchHistory);
    }

    @Transactional
    public void deleteAllWatchFilm() {
        // Lấy thông tin user từ context
        User user = SecurityUtils.getCurrentUserLogin();

        // Xóa tất cả lịch sử xem phim của user hiện tại
        watchHistoryRepository.deleteAllByUser_Id(user.getId());
    }

    public WatchHistoryDto getWatchHistory(Integer filmId, Integer episodeId) {
        // Lấy thông tin user từ context
        User user = SecurityUtils.getCurrentUserLogin();

        if (user == null) {
            return null;
        }

        return watchHistoryRepository.findByUser_IdAndFilm_IdAndEpisode_Id(user.getId(), filmId, episodeId)
                .orElse(null);
    }
}
