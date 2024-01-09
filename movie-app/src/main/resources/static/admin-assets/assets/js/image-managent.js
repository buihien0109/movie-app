const btnModalImage = document.getElementById("btn-modal-image");
const btnChoseImage = document.getElementById("btn-chose-image");
const inputImageEl  = document.getElementById("input-image");
const btnDeleteImage = document.getElementById("btn-delete-image");
const imageContainerEl  = document.querySelector(".image-container");
const imageEl = document.getElementById("image");

let imageList = [];

btnModalImage.addEventListener("click", async () => {
    try {
        // Goi API
        const res = await axios.get("/api/v1/files");

        // Lưu lại kết quả trả về từ server
        imageList = res.data;

        // Hien thi hinh anh
        renderPagination(imageList);
    } catch (err) {
        console.log(err)
    }
})

// Hiển thị danh sách image
function renderImages(imageList) {
    // Xóa hết nd đang có trước khi render
    imageContainerEl.innerHTML = "";

    // Tạo nd
    let html = ""
    imageList.forEach(image => {
        html += `
            <div class="image-item" onclick="choseImage(this)" data-id="${image.id}">
                <img src="/api/v1/files/${image.id}" alt="">
            </div>
        `
    })

    // Insert nd
    imageContainerEl.innerHTML = html;
}

// Hiển thị phần phân trang
function renderPagination(imageList) {
    $('.image-pagination-container').pagination({
        dataSource: imageList,
        pageSize : 12,
        callback: function (data, pagination) {
            renderImages(data);
        }
    })
}

// Chọn ảnh
function choseImage(ele) {
    // Xóa hết selected
    const imageSelected = document.querySelector(".image-item.selected");
    if(imageSelected) {
        imageSelected.classList.remove("selected")
    }

    // Thêm selected
    ele.classList.add("selected");

    // Active 2 nút chức năng
    btnChoseImage.disabled = false;
    btnDeleteImage.disabled = false;
}

// Xóa ảnh
btnDeleteImage.addEventListener("click", async () => {
    try {
        const imageSelected = document.querySelector(".image-item.selected");
        const imageId = +imageSelected.dataset.id

        // Xóa trên server
        await axios.delete(`/api/v1/files/${imageId}`)

        // Xóa trong mảng ban đầu
        imageList = imageList.filter(i => i.id !== imageId);
        renderPagination(imageList);

        // Disable 2 nút chức năng
        btnChoseImage.disabled = true;
        btnDeleteImage.disabled = true;
    } catch (e) {
        console.log(e)
    }
})

// Chọn ảnh Preview
btnChoseImage.addEventListener("click", () => {
    // Preview ảnh
    const imageSelected = document.querySelector(".image-item.selected");
    const url = imageSelected.querySelector("img").getAttribute("src");
    imageEl.src = url;

    // Đóng modal
    $("#modal-image").modal("hide");

    // Disable 2 nút chức năng
    btnChoseImage.disabled = true;
    btnDeleteImage.disabled = true;
})

// Upload file
inputImageEl.addEventListener("change", (event) => {
    const file = event.target.files[0]

    // Tạo đối tượng formData
    const formData = new FormData();
    formData.append("file", file)

    // Gọi API
    axios.post(`/api/v1/files`, formData)
        .then(res => {
            imageList.unshift(res.data);
            renderPagination(imageList);
        })
        .catch(err => {
            console.log(err)
            toastr.error(err.response.data.message)
        })
})