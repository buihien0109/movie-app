//Initialize Select2 Elements
$(".select2").select2({
    placeholder: "Chọn role",
});

$.validator.addMethod(
    "userRole",
    function (value, element) {
        const userRole = roles.find(e => e.name === "USER")
        const rolesSelected = $("#roles").val().map(e => Number(e))
        return rolesSelected.includes(userRole.roleId);
    },
    "Role USER phải được chọn"
);

$('#form-create-user').validate({
    rules: {
        username: {
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
        roles: {
            required: true,
            userRole: true
        },
    },
    messages: {
        username: {
            required: "Tên user không được để trống"
        },
        email: {
            required: "Email không được để trống",
            email: "Email không đúng định dạng"
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
const userEmail = document.getElementById("email");
const userPhone = document.getElementById("phone");
const userRoles = $("#roles");
const btnCreate = document.getElementById("btn-create")

// Tạo khóa học
btnCreate.addEventListener("click", async () => {
    if (!$('#form-create-user').valid()) return false;

    const data = {
        username: userName.value,
        email: userEmail.value,
        phone: userPhone.value,
        roleIds: userRoles.val().map(e => Number(e))
    }

    try {
        let res = await axios.post("/api/v1/admin/users", data)

        if (res.data) {
            toastr.success("Tạo user thành công");
            setTimeout(() => {
                window.location.href = `/admin/users/${res.data.userId}/detail`
            }, 1500)
        }
    } catch (e) {
        toastr.error(e.response.data.message);
        console.log(e);
    }
})