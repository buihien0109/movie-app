/*-------------------
    Rating change
--------------------- */
const stars = document.querySelectorAll(".review-product-rating-item");
const ratingMessage = document.querySelector(".review-product-rating-result");
let currentRating = 0;
let currentMessage = "";

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

//-------------------
let idUpdate = null;
let isUpdate = false;
const reviewContent = document.querySelector("#review-content");

// click button to open modal review
const btnCreateReview = document.querySelector(".btn-create-review");
btnCreateReview.addEventListener("click", () => {
    $('#modal-review').modal('show');
})


$('#modal-review').on('hidden.bs.modal', function (event) {
    // clear review content
    reviewContent.value = "";

    // clear rating message
    ratingMessage.textContent = "";

    // reset stars
    resetStars();

    idUpdate = null;
    isUpdate = false;
})

function openModalToUpdateReview(reviewId) {
    // find review by id
    const review = reviews.find(review => review.id === reviewId);

    // set rating
    currentRating = review.rating;
    currentMessage = reviewMessage(currentRating);
    ratingMessage.textContent = `Bạn đã đánh giá ${currentRating} sao. (${currentMessage})`;
    highlightStars(currentRating);

    // set review content
    reviewContent.value = review.comment;

    // set id update
    idUpdate = reviewId;
    isUpdate = true;

    // open modal
    $('#modal-review').modal('show');
}

/*-------------------
    Handle review (create/update)
--------------------- */
const btnSendReview = document.querySelector("#btn-send-review");
btnSendReview.addEventListener("click", () => {
    if (isUpdate) {
        updateReview(idUpdate);
    } else {
        createReview();
    }
})

// validate form
$('#form-review').validate({
    rules: {
        content: {
            required: true
        },
    },
    messages: {
        content: {
            required: "Nội dung không được để trống",
        }
    },
    errorElement: 'span',
    errorPlacement: function (error, element) {
        error.addClass('invalid-feedback');
        element.closest('.form-group').append(error);
    },
    highlight: function (element, errorClass, validClass) {
        $(element).addClass('is-invalid');
    },
    unhighlight: function (element, errorClass, validClass) {
        $(element).removeClass('is-invalid');
    }
});

/*-------------------
    Create review
--------------------- */
const createReview = () => {
    if (!$("#form-review").valid()) {
        return;
    }

    // Nếu chưa đánh giá star thì cảnh báo
    if (currentRating === 0) {
        toastr.warning("Vui lòng đánh giá sao trước khi gửi đánh giá");
        return;
    }

    const review = {
        filmId: film.id,
        rating: currentRating,
        comment: reviewContent.value
    }

    // Send request to server using axios
    axios.post("/api/reviews", review)
        .then(res => {
            if (res.status === 200) {
                // add review to reviews array
                reviews.unshift(res.data);
                renderPagination();

                toastr.success("Đánh giá thành công");
                currentRating = 0;
                currentMessage = "";

                // close modal
                $('#modal-review').modal('hide');
            } else {
                toastr.error("Đánh giá thất bại");
            }
        })
        .catch(err => {
            console.log(err);
            toastr.error(err.response.data.message);
        })
}

/*-------------------
    Delete review
--------------------- */
const deleteReview = id => {
    const isDelete = confirm("Bạn có chắc chắn muốn xóa đánh giá này?");
    if (!isDelete) {
        return;
    }
    // Send request to server using axios
    axios.delete(`/api/reviews/${id}`)
        .then(res => {
            if (res.status === 204) {
                // delete review in reviews array
                const index = reviews.findIndex(review => review.id === id);
                reviews.splice(index, 1);
                renderPagination();

                toastr.success("Xóa đánh giá thành công");
            } else {
                toastr.error("Xóa đánh giá thất bại");
            }
        })
        .catch(err => {
            console.log(err);
            toastr.error(err.response.data.message);
        })
}


/*-------------------
    Update review
--------------------- */
const updateReview = id => {
    if (!$("#form-review").valid()) {
        return;
    }

    // Nếu chưa đánh giá star thì cảnh báo
    if (currentRating === 0) {
        toastr.warning("Vui lòng đánh giá sao trước khi gửi đánh giá");
        return;
    }

    const review = {
        filmId: film.id,
        rating: currentRating,
        comment: reviewContent.value
    }

    // Send request to server using axios
    axios.put(`/api/reviews/${id}`, review)
        .then(res => {
            if (res.status === 200) {
                // update review in reviews array
                const index = reviews.findIndex(review => review.id === id);
                reviews[index] = res.data;
                renderPagination();

                toastr.success("Cập nhật đánh giá thành công");
                currentRating = 0;
                currentMessage = "";

                // close modal
                $('#modal-review').modal('hide');
            } else {
                toastr.error("Cập nhật đánh giá thất bại");
            }
        })
        .catch(err => {
            console.log(err);
            toastr.error(err.response.data.message);
        })
}