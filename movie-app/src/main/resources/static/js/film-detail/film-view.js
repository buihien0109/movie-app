// Tính lượt xem phim ----------------------------------------------------------------------------------
const duration = currentEpisode.duration;
let startTime = 0;
let endTime = 0;
let requestSent = false;  // Biến để kiểm tra xem request đã được gửi chưa
let isReset = false;  // Biến để reset startTime và endTime vừa vào trang

video.addEventListener('timeupdate', function () {
    endTime = video.currentTime;
    if (!isReset) {
        startTime = endTime;
        isReset = true;
    }

    const durationWatched = endTime - startTime;
    const completionPercentage = (durationWatched / duration) * 100;
    if (!requestSent && completionPercentage >= 50) {
        sendViewLogRequest(film.id);
        requestSent = true;  // Đánh dấu là request đã được gửi
    }
});

video.addEventListener('play', function () {
    startTime = video.currentTime;
});

video.addEventListener('ended', function () {
    // Check if request has not been sent and completionPercentage is greater than or equal to 50%
    const durationWatched = endTime - startTime;
    const completionPercentage = (durationWatched / duration) * 100;
    console.log("completionPercentage 1: " + completionPercentage);
    if (!requestSent && completionPercentage >= 50) {
        sendViewLogRequest(film.id);
        requestSent = true;  // Đánh dấu là request đã được gửi
    }
});


function sendViewLogRequest(filmId) {
    // Call API sử dụng axios
    axios.post(`/api/view-film-logs?filmId=${filmId}`)
        .then(response => {
        })
        .catch(error => {
            console.log(error);
            toastr.error(error.response.data.message);
        });
}