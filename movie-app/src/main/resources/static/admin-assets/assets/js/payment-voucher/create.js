// convert price to number when user submit
const formatPrice = (price) => {
    return parseInt(price.replace(/,/g, ""));
}

// Validate form
$.validator.addMethod(
    "validateMinValue",
    function (value, element) {
        // get value of type
        return formatPrice(value) > 0
    },
    "Giá trị phải lớn hơn 0"
);

$.validator.addMethod(
    "validateInteger",
    function (value, element) {
        // check if value is number
        return Number.isInteger(Number(formatPrice(value)))
    },
    "Giá trị phải là số nguyên"
);

$('#form-create-payment-voucher').validate({
    rules: {
        purpose: {
            required: true
        },
        amount: {
            required: true,
            validateInteger: true,
            validateMinValue: true,
        },
    },
    messages: {
        purpose: {
            required: "Mục đích chi không được để trống"
        },
        amount: {
            required: "Số tiền không được để trống",
            validateInteger: "Số tiền phải là số nguyên",
            validateMinValue: "Số tiền phải lớn hơn 0",
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
const purposeEl = document.getElementById("purpose");
const amountEl = document.getElementById("amount");
const noteEl = document.getElementById("note");
const btnCreate = document.getElementById("btn-create")

// format amount when user input
amountEl.addEventListener('keyup', function (event) {
    const value = event.target.value;
    event.target.value = value.replace(/\D/g, "").replace(/\B(?=(\d{3})+(?!\d))/g, ",");
});

btnCreate.addEventListener("click", async () => {
    if ($('#form-create-payment-voucher').valid() === false) return

    const data = {
        purpose: purposeEl.value,
        amount: formatPrice(amountEl.value),
        note: noteEl.value,
    }

    try {
        let res = await axios.post("/api/v1/admin/payment_vouchers", data)

        if (res.status === 200) {
            toastr.success("Tạo phiếu chi thành công");
            setTimeout(() => {
                window.location.href = `/admin/payment_vouchers/${res.data.id}/detail`
            }, 1500)
        } else {
            toastr.error("Tạo phiếu chi thất bại");
        }
    } catch (e) {
        console.log(e);
        toastr.error(e.response.data.message);
    }
})