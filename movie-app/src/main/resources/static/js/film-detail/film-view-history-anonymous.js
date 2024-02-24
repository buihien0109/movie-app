// Function cập nhật lịch sử xem phim vào localStorage
function updateWatchHistoryLocal(currentTime) {
    // Lấy lịch sử xem phim từ localStorage
    let watchHistory = JSON.parse(localStorage.getItem('watchHistory')) || [];

    // Tạo dữ liệu lịch sử xem phim hiện tại
    const currentWatchData = {
        filmId: film.id,
        episodeId: currentEpisode.id,
        duration: parseFloat(currentTime),
    };

    // Tìm vị trí của dữ liệu lịch sử xem phim hiện tại trong mảng lịch sử xem phim
    const existingWatchDataIndex = watchHistory.findIndex(data => data.filmId === film.id && data.episodeId === currentEpisode.id);
    if (existingWatchDataIndex !== -1) {
        watchHistory[existingWatchDataIndex] = currentWatchData;
    } else {
        watchHistory.push(currentWatchData);
    }

    // Lưu lịch sử xem phim vào localStorage
    localStorage.setItem('watchHistory', JSON.stringify(watchHistory));
}

// Lắng nghe sự kiện pause video
video.addEventListener('pause', function () {
    const currentTime = video.currentTime;

    // Cập nhật lịch sử xem phim vào localStorage
    updateWatchHistoryLocal(currentTime);
});

// Lắng nghe sự kiện trước khi đóng trang
window.addEventListener('beforeunload', function () {
    const currentTime = video.currentTime;

    // Cập nhật lịch sử xem phim vào localStorage
    updateWatchHistoryLocal(currentTime);
});

// Trigger video khi vào trang hoặc click vào tập phim --------------------------------------------------
function getWatchHistory() {
    const filmId = film.id;
    const episodeId = currentEpisode.id;

    // Lấy lịch sử xem phim từ localStorage
    const watchHistory = JSON.parse(localStorage.getItem('watchHistory')) || [];

    // Tìm và trả về dữ liệu lịch sử xem phim nếu nó tồn tại
    return watchHistory.find(data => data.filmId === filmId && data.episodeId === episodeId);
}

function setVideoTimeFromHistory() {
    const userWatchData = getWatchHistory();

    if (userWatchData) {
        const {duration} = userWatchData;
        if (!isNaN(duration) && isFinite(duration)) {
            if (duration < currentEpisode.duration) {
                video.currentTime = duration;
            } else {
                video.currentTime = 0;
            }
        } else {
            video.currentTime = 0;
        }
    }
}

document.addEventListener('DOMContentLoaded', setVideoTimeFromHistory);