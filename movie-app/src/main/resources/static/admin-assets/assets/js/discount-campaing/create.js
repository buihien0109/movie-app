// init daterangepicker
$('#date').daterangepicker()

const formatPrice = (price) => {
    return parseInt(price.replace(/,/g, ""));
}

$.validator.addMethod(
    "validateMinValue",
    function (value, element) {
        // get value of type
        return formatPrice(value) > 0
    },
    "Giá trị giảm phải lớn hơn 0"
);

$.validator.addMethod(
    "validateMaxValue",
    function (value, element) {
        // get value of type
        const type = document.getElementById('type').value
        if (type === 'PERCENT') {
            return formatPrice(value) <= 100
        } else {
            return true
        }
    },
    "Giá trị giảm phải nhỏ hơn 100"
);

$.validator.addMethod(
    "validateInteger",
    function (value, element) {
        // check if value is number
        return Number.isInteger(Number(formatPrice(value)))
    },
    "Giá trị giảm phải là số nguyên"
);

// validate form
$('#form-create-discount-campaing').validate({
    rules: {
        name: {
            required: true
        },
        description: {
            required: true
        },
        type: {
            required: true
        },
        value: {
            required: true,
            validateMinValue: true,
            validateMaxValue: true,
            validateInteger: true
        },
        status: {
            required: true
        },
    },
    messages: {
        name: {
            required: "Tên chiến dịch giảm giá không được để trống"
        },
        description: {
            required: "Mô tả không được để trống",
        },
        type: {
            required: "Hình thức giảm giá không được để trống"
        },
        value: {
            required: "Giá trị giảm không được để trống",
            validateMinValue: "Giá trị giảm phải lớn hơn 0",
            validateMaxValue: "Giá trị giảm phải nhỏ hơn 100",
            validateInteger: "Giá trị giảm phải là số nguyên"
        },
        status: {
            required: "Trạng thái không được để trống"
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

// format value when user input
const input = document.getElementById('value')
input.addEventListener('input', () => {
    const value = input.value
    const type = document.getElementById('type').value
    if (type === 'PERCENT') {
        if (formatPrice(value) > 100) {
            input.value = 100
        } else {
            if (formatPrice(value) < 0) {
                input.value = 0
            }
        }
    } else {
        if (formatPrice(value) < 0) {
            input.value = 0
        }
    }

    // format price with type is AMOUNT or SAME_PRICE
    if (type === 'AMOUNT' || type === 'SAME_PRICE') {
        input.value = input.value.replace(/\D/g, '').replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    }
})

// handle create discount campaign when click button using vanilla javascript and axios
const btnCreate = document.getElementById('btn-create')
btnCreate.addEventListener('click', () => {
    if (!$('#form-create-discount-campaing').valid()) {
        return
    }

    const name = document.getElementById('name').value
    const description = document.getElementById('description').value
    const type = document.getElementById('type').value
    const value = document.getElementById('value').value
    const status = document.getElementById('status').value
    const date = document.getElementById('date').value

    // get date from daterangepicker
    const dateArr = date.split(' - ')
    const startDate = dateArr[0]
    const endDate = dateArr[1]

    const data = {
        name: name,
        description: description,
        discountType: type,
        discountValue: value,
        status: status,
        startDate: new Date(startDate),
        endDate: new Date(endDate)
    }
    console.log(data)

    axios.post('/api/v1/admin/discount-campaigns', data)
        .then(res => {
            if (res.status === 200) {
                toastr.success('Tạo khuyến mại thành công')
                setTimeout(() => {
                    window.location.href = `/admin/discount-campaigns/${res.data.campaignId}/detail`
                }, 1500)
            } else {
                toastr.error('Tạo khuyến mại thất bại')
            }
        })
        .catch(err => {
            console.log(err)
            toastr.error(err.response.data.message)
        })
})
