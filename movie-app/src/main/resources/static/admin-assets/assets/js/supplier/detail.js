$('#form-update-supplier').validate({
    rules: {
        name: {
            required: true
        },
        email: {
            required: true,
            email: true,
        },
        phone: {
            required: true,
            pattern: /^(0\d{9}|(\+84|84)[1-9]\d{8})$/
        },
        address: {
            required: true
        },
    },
    messages: {
        name: {
            required: "Tên nhà cung cấp không được để trống"
        },
        email: {
            required: "Email không được để trống",
            email: "Email không đúng định dạng"
        },
        phone: {
            required: "Số điện thoại không được để trống",
            pattern: "Số điện thoại không đúng định dạng",

        },
        address: {
            required: "Địa chỉ không được để trống"
        },
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

// Truy cập vào các thành phần
const nameEl = document.getElementById("name");
const emailEl = document.getElementById("email");
const phoneEl = document.getElementById("phone");
const addressEl = document.getElementById("address");
const btnUpdate = document.getElementById("btn-update")

btnUpdate.addEventListener("click", async () => {
    if ($('#form-update-supplier').valid() === false) return
    const data = {
        name: nameEl.value,
        email: emailEl.value,
        phone: phoneEl.value,
        address: addressEl.value
    }
    console.log(data)

    try {
        let res = await axios.put(`/api/v1/admin/suppliers/${supplier.supplierId}`, data)

        if (res.status === 200) {
            toastr.success("Cập nhật nhà cung cấp thành công");
        } else {
            toastr.error("Cập nhật nhà cung cấp thất bại");
        }
    } catch (e) {
        toastr.error(e.response.data.message);
        console.log(e);
    }
})
// add event listener to avatar input change
const thumbnailInput = document.getElementById('avatar');
const thumbnailPreview = document.getElementById('avatar-preview');
thumbnailInput.addEventListener('change', (e) => {
    const file = e.target.files[0];

    // create form data with key file and value is file in input
    const formData = new FormData();
    formData.append('file', file);

    // Send post request to url /api/v1/users/update-avatar, then check request status and set src for avatar preview
    axios.post(`/api/v1/admin/suppliers/${supplier.supplierId}/update-thumbnail`, formData)
        .then(res => {
            if (res.status === 200) {
                thumbnailPreview.src = res.data;
                toastr.success('Cập nhật thumbnail thành công');
            } else {
                toastr.error('Cập nhật thumbnail thất bại');
            }
        })
        .catch(err => {
            console.log(err)
            toastr.error(err.response.data.message);
        });
});