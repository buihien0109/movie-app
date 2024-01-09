const bannerContainer = document.getElementById('banner-container');
// Khởi tạo Sortable
new Sortable(bannerContainer, {
    animation: 150, // Thời gian animation
    ghostClass: 'bg-info', // Class của item khi đang drag
    onEnd: function (evt) {
        // Xử lý khi thay đổi vị trí của các banner
        const items = Array.from(bannerContainer.children);
        const newOrder = items.map((item) => item.dataset.id);
    },
});

// Xử lý khi click vào nút cập nhật và gửi request lên server sử dụng Axios
const btnUpdate = document.getElementById('btn-update');
btnUpdate.addEventListener('click', function () {
    const items = Array.from(bannerContainer.children);
    const newOrder = items.map((item) => Number(item.dataset.id));

    // In ra thứ tự mới của các banner
    console.log('Thứ tự mới:', newOrder);

    // Gửi request lên server
    axios.post('/api/v1/admin/banners/sort', newOrder)
        .then(function (response) {
            if (response.status === 200) {
                toastr.success('Cập nhật vị trí các banner thành công!');
            } else {
                toastr.error('Cập nhật vị trí các banner thất bại!');
            }
        })
        .catch(function (error) {
            console.log(error);
            toastr.error(error.response.data.message);
        });
});