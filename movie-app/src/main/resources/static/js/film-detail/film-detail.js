const reviewMessage = (rating) => {
    switch (rating) {
        case 1:
            return "Không thích";
        case 2:
            return "Rất tệ";
        case 3:
            return "Tệ";
        case 4:
            return "Dưới trung bình";
        case 5:
            return "Trung bình";
        case 6:
            return "Khá ổn";
        case 7:
            return "Tốt";
        case 8:
            return "Rất tốt";
        case 9:
            return "Tuyệt vời";
        case 10:
            return "Xuất sắc";
        default:
            return "";
    }
}

const formatDate = dateString => {
    const date = new Date(dateString);
    const day = `0${date.getDate()}`.slice(-2);
    const month = `0${date.getMonth() + 1}`.slice(-2);
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
}

/*-------------------
    render review
--------------------- */
const renderReview = review => {
    return `
            <div class="rating-item d-flex align-items-center mb-3 pb-3">
                    <div class="rating-avatar">
                        <img src="${review.user.avatar}"
                             alt="${review.user.name}">
                    </div>
                    <div class="rating-info ms-3">
                        <div class="d-flex align-items-center">
                            <p class="rating-name mb-0">
                                <strong>${review.user.name}</strong>
                            </p>
                            <p class="rating-time mb-0 ms-2">${formatDate(review.createdAt)}</p>
                        </div>
                        <div class="rating-star">
                            <p class="mb-0 fw-bold">
                                <span class="rating-icon"><i class="fa fa-star"></i></span>
                                <span>${review.rating}/10 ${reviewMessage(review.rating)}</span>
                            </p>
                        </div>
                        <p class="rating-content mt-1 mb-0 text-muted">${review.comment}</p>
                        ${currentUser && currentUser.id === review.user.id ? `
                            <div>
                                <button class="border-0 bg-transparent btn-edit-review text-primary me-2 text-decoration-underline" onclick="openModalToUpdateReview(${review.id})">Sửa</button>
                                <button class="border-0 bg-transparent btn-delete-review text-danger text-decoration-underline" onclick="deleteReview(${review.id})">Xóa</button>
                            </div>
                        ` : ""}
                    </div>
                </div>
            </div>
            `
}

const renderReviews = (reviewList) => {
    const reviewListEl = document.querySelector(".review-list");
    reviewListEl.innerHTML = "";
    let html = "";
    reviewList.forEach(review => {
        html += renderReview(review);
    })
    reviewListEl.innerHTML = html;

}

const renderPagination = () => {
    $('#review-pagination').pagination({
        dataSource: reviews,
        pageSize: 5,
        callback: function (data, pagination) {
            renderReviews(data);
        },
        hideOnlyOnePage: true,
    })
}

renderPagination();

// Wish list ---------------------------------------------------------------------------------------------
function addToWishList(filmId) {
    axios.post('/api/wishlists', {filmId})
        .then(res => {
            if (res.status === 200) {
                toastr.success("Thêm vào danh sách yêu thích thành công");

                // update button
                const btnWishListContainer = document.querySelector("#btn-wishlist-container");
                btnWishListContainer.innerHTML = `
                    <button class="d-inline-block btn bg-dark px-5 py-2 mt-3 rounded text-white" onclick="removeFromWishList(${filmId})">
                        <span><i class="fa-solid fa-trash-can"></i></span> Loại khỏi yêu thích
                    </button>
                `
            } else {
                toastr.error("Thêm vào danh sách yêu thích thất bại");
            }
        }).catch(err => {
        console.log(err);
        toastr.error(err.response.data.message)
    })
}

function removeFromWishList(filmId) {
    axios.delete(`/api/wishlists?filmId=${filmId}`)
        .then(res => {
            if (res.status === 204) {
                toastr.success("Xóa khỏi danh sách yêu thích thành công");

                // update button
                const btnWishListContainer = document.querySelector("#btn-wishlist-container");
                btnWishListContainer.innerHTML = `
                    <button class="d-inline-block btn bg-dark px-5 py-2 mt-3 rounded text-white" onclick="addToWishList(${filmId})">
                        <span><i class="fa-solid fa-heart"></i></span> Thêm vào yêu thích
                    </button>
                `
            } else {
                toastr.error("Xóa khỏi danh sách yêu thích thất bại");
            }
        }).catch(err => {
        console.log(err);
        toastr.error(err.response.data.message)
    })
}

// Tính lượt xem phim ----------------------------------------------------------------------------------
const duration = currentEpisode.video.duration;
let startTime = 0;
let endTime = 0;
let requestSent = false;  // Biến để kiểm tra xem request đã được gửi chưa
let isReset = false;  // Biến để reset startTime và endTime vừa vào trang

video.addEventListener('timeupdate', function () {
    endTime = video.currentTime;
    if(!isReset) {
        startTime = endTime;
        isReset = true;
    }

    console.log("startTime: " + startTime);
    console.log("endTime: " + endTime);
    const durationWatched = endTime - startTime;
    const completionPercentage = (durationWatched / duration) * 100;
    console.log("completionPercentage 1: " + completionPercentage);
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
        .then(response => {})
        .catch(error => {
            console.log(error);
            toastr.error(error.response.data.message);
        });
}