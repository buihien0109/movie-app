$('#form-create-supplier').validate({
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
const btnCreate = document.getElementById("btn-create")

btnCreate.addEventListener("click", async () => {
    if ($('#form-create-supplier').valid() === false) return

    const data = {
        name: nameEl.value,
        email: emailEl.value,
        phone: phoneEl.value,
        address: addressEl.value
    }

    try {
        let res = await axios.post("/api/v1/admin/suppliers", data)

        if (res.status === 200) {
            toastr.success("Tạo nhà cung cấp thành công");
            setTimeout(() => {
                window.location.href = `/admin/suppliers/${res.data.supplierId}/detail`
            }, 1500)
        } else {
            toastr.error("Tạo nhà cung cấp thất bại");
        }
    } catch (e) {
        toastr.error(e.response.data.message);
        console.log(e);
    }
})