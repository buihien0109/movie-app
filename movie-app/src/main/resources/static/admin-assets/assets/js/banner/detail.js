$('#form-update-banner').validate({
    rules: {
        name: {
            required: true
        },
        linkRedirect: {
            required: true
        }
    },
    messages: {
        name: {
            required: "Tên banner không được để trống"
        },
        linkRedirect: {
            required: "Link không được để trống"
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

// send request to server when click button create using axios
const btnUpdate = document.getElementById('btn-update');
btnUpdate.addEventListener('click', function () {
    if (!$('#form-update-banner').valid()) return;

    const name = document.getElementById('name').value;
    const linkRedirect = document.getElementById('link-redirect').value;
    const status = document.getElementById('status').value;
    const url = document.getElementById('image').src;

    const data = {
        name: name,
        linkRedirect: linkRedirect,
        status: status === "true",
        url: url
    };

    axios.put(`/api/v1/admin/banners/${banner.id}`, data)
        .then(function (response) {
            if (response.status === 200) {
                toastr.success('Cập nhật banner thành công');
            } else {
                toastr.error('Cập nhật banner thất bại');
            }
        })
        .catch(function (error) {
            console.log(error);
            toastr.error(error.response.data.message);
        });
});

// send request to server when click button delete using axios
const btnDelete = document.getElementById('btn-delete');
btnDelete.addEventListener('click', function () {
    const isConfirm = confirm('Bạn có chắc chắn muốn xóa banner này?');
    if (!isConfirm) return;
    axios.delete(`/api/v1/admin/banners/${banner.id}`)
        .then(function (response) {
            if (response.status === 200) {
                toastr.success('Xóa banner thành công');
                setTimeout(function () {
                    window.location.href = '/admin/banners';
                }, 1500);
            } else {
                toastr.error('Xóa banner thất bại');
            }
        })
        .catch(function (error) {
            console.log(error);
            toastr.error(error.response.data.message);
        });
});
