// render products from products to body of table
const tableReviewBody = document.querySelector('#table-review tbody');
const renderReviews = (reviews) => {
    let html = '';
    reviews.forEach(review => {
        html += `
                    <tr>
                        <td>${review.authorName}</td>
                        <td>
                            <span>${review.rating}</span>
                            <span style="color: #EDBB0E"><i class="fas fa-star"></i></span>
                        </td>
                        <td>${review.comment}</td>
                        <td>${formatDateTime(review.updatedAt)}</td>
                        <td>${review.status}</td>
                        <td>
                            ${review.status === 'PENDING'
            ? `
                                     <button class="btn btn-success btn-sm" onclick="updateReviewStatus(${review.reviewId}, 'ACCEPTED')">
                                        <i class="fas fa-check"></i>
                                    </button>
                                    <button class="btn btn-danger btn-sm" onclick="updateReviewStatus(${review.reviewId}, 'REJECTED')">
                                        <i class="fas fa-times"></i>
                                    </button>
                                `
            : ''
        }
                            ${review.status === 'ACCEPTED'
            ? `
                                    <button type="button" class="btn btn-primary btn-sm" onclick="openModalReview(${review.reviewId})">
                                        <i class="fas fa-pencil-alt"></i>
                                    </button>
                                    <button class="btn btn-danger btn-sm" onclick="deleteReview(${review.reviewId})">
                                        <i class="fas fa-trash-alt"></i>
                                    </button>
                                `
            : ''
        }

                            ${review.status === 'REJECTED'
            ? `
                                    <button class="btn btn-danger btn-sm" onclick="deleteReview(${review.reviewId})">
                                        <i class="fas fa-trash-alt"></i>
                                    </button>
                                `
            : ''
        }

                        </td>
                    </tr>
                `;
    })
    tableReviewBody.innerHTML = html;
}

// render pagination using pagination.js
const renderPaginationReview = () => {
    $('#review-pagination').pagination({
        dataSource: reviewList,
        className: 'paginationjs-theme-blue paginationjs-big',
        callback: function (data, pagination) {
            renderReviews(data);
        },
        hideOnlyOnePage: true
    })
}

/*-------------------
    Rating change
--------------------- */
const stars = document.querySelectorAll(".review-product-rating-item");
const ratingMessage = document.querySelector(".review-product-rating-result");
let currentRating = 0;
let currentMessage = "";
let reviewIdUpdate = null;

stars.forEach((star) => {
    star.addEventListener("mouseover", () => {
        resetStars();
        const rating = parseInt(star.getAttribute("data-rating"));
        highlightStars(rating);
    });

    star.addEventListener("mouseout", () => {
        resetStars();
        highlightStars(currentRating);
    });

    star.addEventListener("click", () => {
        currentRating = parseInt(star.getAttribute("data-rating"));
        currentMessage = star.getAttribute("data-message");
        ratingMessage.textContent = `Bạn đã đánh giá ${currentRating} sao. (${currentMessage})`;
        highlightStars(currentRating);
    });
});

function resetStars() {
    stars.forEach((star) => {
        star.classList.remove("active");
    });
}

function highlightStars(rating) {
    stars.forEach((star) => {
        const starRating = parseInt(star.getAttribute("data-rating"));
        if (starRating <= rating) {
            star.classList.add("active");
        }
    });
}

function reviewMessage(rating) {
    switch (rating) {
        case 1:
            return "Không thích";
        case 2:
            return "Tạm được";
        case 3:
            return "Bình thường";
        case 4:
            return "Hài lòng";
        case 5:
            return "Tuyệt vời";
        default:
            return "";
    }
}

// add event to modal review hidden
$('#modal-review').on('hidden.bs.modal', function (e) {
    // reset rating
    currentRating = 0;
    currentMessage = "";
    resetStars();
    ratingMessage.textContent = "";

    // reset review id
    reviewIdUpdate = null;
})

const openModalReview = reviewId => {
    // find review by id
    const review = reviewList.find(review => review.reviewId === reviewId);
    // set rating
    currentRating = review.rating;
    highlightStars(currentRating);
    currentMessage = reviewMessage(currentRating);
    // set rating message
    ratingMessage.textContent = `Bạn đã đánh giá ${currentRating} sao. (${currentMessage})`;

    // set review content
    document.querySelector('#review-content').value = review.comment;

    // open modal
    $('#modal-review').modal('show');

    // set review id
    reviewIdUpdate = reviewId;
}

// update review
const btnUpdateReview = document.querySelector('#btn-update-review');
btnUpdateReview.addEventListener('click', () => {
    const reviewContent = document.querySelector('#review-content').value;
    const review = {
        rating: currentRating,
        comment: reviewContent
    }
    // Send request to server using axios
    axios.put(`/api/v1/admin/reviews/${reviewIdUpdate}`, review)
        .then(res => {
            // update review in reviews array
            const index = reviewList.findIndex(review => review.reviewId === reviewIdUpdate);
            reviewList[index] = res.data;
            renderPaginationReview();

            toastr.success("Cập nhật đánh giá thành công");
            $('#modal-review').modal('hide');

            // reset rating
            currentRating = 0;
            currentMessage = "";
            resetStars();
            ratingMessage.textContent = "";

            // reset review id
            reviewIdUpdate = null;
        })
        .catch(err => {
            console.log(err);
            toastr.error("Cập nhật đánh giá thất bại");
        })
})

const deleteReview = id => {
    const isDelete = confirm("Bạn có chắc chắn muốn xóa đánh giá này?");
    if (!isDelete) {
        return;
    }
    // Send request to server using axios
    axios.delete(`/api/v1/admin/reviews/${id}`)
        .then(res => {
            // delete review in reviews array
            const index = reviewList.findIndex(review => review.reviewId === id);
            reviewList.splice(index, 1);
            renderPaginationReview();

            toastr.success("Xóa đánh giá thành công");
        })
        .catch(err => {
            console.log(err);
            toastr.error("Xóa đánh giá thất bại");
        })
}

// handle accept review
const updateReviewStatus = (id, status) => {
    const isAccept = confirm("Bạn có chắc chắn muốn duyệt đánh giá này?");
    if (!isAccept) {
        return;
    }
    // Send request to server using axios
    axios.put(`/api/v1/admin/reviews/${id}/update-status?status=${status}`)
        .then(res => {
            // update review in reviews array
            const index = reviewList.findIndex(review => review.reviewId === id);
            reviewList[index] = res.data;
            renderPaginationReview();

            toastr.success("Duyệt đánh giá thành công");
        })
        .catch(err => {
            console.log(err);
            toastr.error("Duyệt đánh giá thất bại");
        })
}

renderPaginationReview()