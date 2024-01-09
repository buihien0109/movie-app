// upload main image
const inputUploadMainImage = document.getElementById('input-upload-main-image');
const mainImagePreview = document.querySelector('.product-main-image-item img');

inputUploadMainImage.addEventListener('change', function (e) {
    const file = e.target.files[0];

    // create form data with key file and value is file in input
    const formData = new FormData();
    formData.append('file', file);

    // Send post request to url /api/v1/users/update-avatar, then check request status and set src for avatar preview
    axios.post(`/api/v1/admin/products/${product.productId}/images/upload-main-image`, formData)
        .then(res => {
            if (res.status === 200) {
                mainImagePreview.src = res.data.imageUrl;
                toastr.success('Cập nhật ảnh đại diện thành công');
            } else {
                toastr.error('Cập nhật ảnh đại diện thất bại');
            }
        })
        .catch(err => {
            console.log(err)
            toastr.error(err.response.data.message);
        });
});

// upload sub images
const inputUploadSubImage = document.getElementById('input-upload-sub-image');
inputUploadSubImage.addEventListener('change', function (e) {
    const file = e.target.files[0];

    // create form data with key file and value is file in input
    const formData = new FormData();
    formData.append('file', file);

    // Send post request to url /api/v1/users/update-avatar, then check request status and set src for avatar preview
    axios.post(`/api/v1/admin/products/${product.productId}/images/upload-sub-image`, formData)
        .then(res => {
            if (res.status === 200) {
                const productImageItemUploadSubImage = document.querySelector('.product-image-item-upload-sub-image');
                productImageItemUploadSubImage.insertAdjacentHTML('beforebegin', `
                    <div class="product-image-item product-sub-image-item mr-4 mb-4" data-image-sub-id="${res.data.imageId}">
                        <img src="${res.data.imageUrl}" alt="Ảnh mô tả" class="img-fluid">
                        <span class="btn-action btn-action-delete" onclick="deleteSubImage(${res.data.imageId})"><i class="fas fa-times-circle"></i></span>
                    </div>
                `);
                toastr.success('Cập nhật ảnh mô tả thành công');
            } else {
                toastr.error('Cập nhật ảnh mô tả thất bại');
            }
        })
        .catch(err => {
            console.log(err)
            toastr.error(err.response.data.message);
        });
});

// delete sub image
const deleteSubImage = (imageId) => {
    const isConfirm = confirm('Bạn có chắc chắn muốn xóa ảnh này?');
    if (isConfirm) {
        axios.delete(`/api/v1/admin/products/${product.productId}/images/${imageId}`)
            .then(res => {
                if (res.status === 200) {
                    const productSubImageItem = document.querySelector(`.product-sub-image-item[data-image-sub-id="${imageId}"]`);
                    productSubImageItem.remove();
                    toastr.success('Xóa ảnh mô tả thành công');
                } else {
                    toastr.error('Xóa ảnh mô tả thất bại');
                }
            })
            .catch(err => {
                console.log(err)
                toastr.error(err.response.data.message);
            });
    }
}