
//Initialize Select2 Elements
$(".select2").select2({
    placeholder: "Chọn role",
});
$("#roles").val(user.roles.map(e => e.roleId)).trigger("change");

// validate form
$.validator.addMethod(
    "userRole",
    function (value, element) {
        const userRole = roles.find(e => e.name === "USER")
        const rolesSelected = $("#roles").val().map(e => Number(e))
        return rolesSelected.includes(userRole.roleId);
    },
    "Role USER phải được chọn"
);

$('#form-update-user').validate({
    rules: {
        username: {
            required: true
        },
        phone: {
            required: true,
            pattern: /^(0\d{9}|(\+84|84)[1-9]\d{8})$/
        },
        roles: {
            required: true,
            userRole: true
        },
    },
    messages: {
        username: {
            required: "Tên user không được để trống"
        },
        phone: {
            required: "Số điện thoại không được để trống",
            pattern: "Số điện thoại không đúng định dạng",

        },
        roles: {
            required: "Role không được để trống",
            userRole: "Role USER phải được chọn"
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
const userName = document.getElementById("username");
const userPhone = document.getElementById("phone");
const userRoles = $("#roles");
const btnUpdate = document.getElementById("btn-update")

// Cập nhật user
btnUpdate.addEventListener("click", async () => {
    if (!$('#form-update-user').valid()) return;

    const data = {
        username: userName.value,
        phone: userPhone.value,
        roleIds: userRoles.val().map(e => Number(e))
    }
    console.log(data);

    try {
        let res = await axios.put(`/api/v1/admin/users/${user.userId}`, data)

        if (res.data) {
            toastr.success("Cập nhật thành công");
        }
    } catch (e) {
        toastr.error(e.response.data.message);
        console.log(e);
    }
})

// add event listener to avatar input change
const avatarInput = document.getElementById('avatar');
const avatarPreview = document.getElementById('avatar-preview');
avatarInput.addEventListener('change', (e) => {
    const file = e.target.files[0];

    // create form data with key file and value is file in input
    const formData = new FormData();
    formData.append('file', file);

    // Send post request to url /api/v1/users/update-avatar, then check request status and set src for avatar preview
    axios.post(`/api/v1/admin/users/${user.userId}/update-avatar`, formData)
        .then(res => {
            if (res.status === 200) {
                avatarPreview.src = res.data;
                toastr.success('Cập nhật avatar thành công');
            }
        })
        .catch(err => {
            console.log(err)
            toastr.error('Cập nhật avatar thất bại');
        });
});

// Reset password
const btnResetPassword = document.getElementById('btn-reset-password');
btnResetPassword.addEventListener('click', async () => {
    const isConfirm = confirm('Bạn có chắc chắn muốn reset mật khẩu?');
    if (!isConfirm) return;
    try {
        let res = await axios.put(`/api/v1/admin/users/${user.userId}/reset-password`);
        if (res.status === 200) {
            toastr.success(`Reset mật khẩu thành công. Mật khẩu mới là: ${res.data}`);
        }
    } catch (e) {
        toastr.error('Reset mật khẩu thất bại');
    }
})