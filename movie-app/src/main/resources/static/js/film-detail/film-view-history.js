// Lưu lịch sử xem phim vào localStorage và gửi lên server ---------------------------------------------
// Function gửi lịch sử xem phim lên server
function sendWatchHistoryToServer(data) {
    axios.post('/api/watch-history', data)
        .then(response => {
            console.log(response);
        })
        .catch(error => {
            console.log(error);
            toastr.error('Có lỗi xảy ra khi gửi dữ liệu lịch sử xem phim lên server');
        });
}

// Lắng nghe sự kiện trước khi đóng trang
window.addEventListener('beforeunload', function () {
    const currentTime = video.currentTime;

    // Gửi lịch sử xem phim lên server
    const data = {
        filmId: film.id,
        episodeId: currentEpisode.id,
        duration: parseFloat(currentTime),
    };

    sendWatchHistoryToServer(data);
});

// Trigger video khi vào trang hoặc click vào tập phim --------------------------------------------------
function setVideoTimeFromHistory() {
    if (watchHistory) {
        const {duration} = watchHistory;
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