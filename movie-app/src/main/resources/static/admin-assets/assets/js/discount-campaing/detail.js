// Init global variables
let productSelected = discountCampaign.products.map(product => {
    return {...product, isChecked: false}
})
let currentType = discountCampaign.discountType
let currentValue = discountCampaign.discountValue

// init daterangepicker
$('#date').daterangepicker({
    startDate: moment(discountCampaign.startDate, "YYYY-MM-DD"), // Set your desired start date
    endDate: moment(discountCampaign.endDate, "YYYY-MM-DD"),   // Set your desired end date
})

// init select2
$('#product').select2({
    theme: 'bootstrap4',
    placeholder: 'Chọn sản phẩm'
})

$('#category').select2({
    theme: 'bootstrap4',
    placeholder: 'Chọn danh mục'
})

// validate form
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
$('#form-update-discount-campaing').validate({
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

// calculate discount price with type and value
const calculateDiscountPrice = (price) => {
    let discountPrice = 0;
    if (currentType === 'PERCENT') {
        discountPrice = price - (price * currentValue / 100)
    } else if (currentType === 'AMOUNT') {
        discountPrice = price - currentValue
    } else if (currentType === 'SAME_PRICE') {
        discountPrice = currentValue
    }
    return discountPrice <= 0 ? 0 : Math.round(discountPrice)
}

// check enable button delete all product
const checkEnableBtnDeleteAllProduct = () => {
    const productChecked = productSelected.filter(product => product.isChecked)
    return productChecked.length > 0;
}

// Render tag
const tableContent = document.querySelector("#table-product tbody")
const renderProducts = (products) => {
    tableContent.innerHTML = "";
    let html = "";
    products.forEach(product => {
        html += `
                    <tr>
                        <td>
                            <input type="checkbox" ${product.isChecked ? "checked" : ""} onchange="selectProductDelete(${product.productId})">
                           </td>
                        <td>${product.name}</td>
                        <td>${formatCurrency(product.price)}</td>
                        <td>${formatCurrency(calculateDiscountPrice(product.price))}</td>
                    </tr>
                `
    })
    tableContent.innerHTML = html;
}

// Open modal category when click button
const btnOpenModalCategory = document.getElementById('btn-open-modal-category')
btnOpenModalCategory.addEventListener('click', () => {
    $('#modal-category').modal('show')
})

// Delete product when click button
const btnDeleteAllProduct = document.getElementById('btn-delete-all-product');
btnDeleteAllProduct.addEventListener('click', () => {
    productSelected = productSelected.filter(product => !product.isChecked)
    if (productSelected.length === 0) {
        inputCheckAll.checked = false
    }
    btnDeleteAllProduct.disabled = !checkEnableBtnDeleteAllProduct();
    renderProducts(productSelected)
})

// Select all product when click button
const btnSelectAllProduct = document.getElementById('btn-select-all-product');
btnSelectAllProduct.addEventListener('click', () => {
    productSelected = productList.map(product => {
        return {...product, isChecked: false}
    });
    renderProducts(productSelected)
})

// when change discount value
const inputDiscountValue = document.getElementById('value')
inputDiscountValue.addEventListener('change', (e) => {
    currentValue = formatPrice(e.target.value)
    renderProducts(productSelected)
})

// when change discount type
const selectDiscountType = document.getElementById('type')
selectDiscountType.addEventListener('change', (e) => {
    currentType = e.target.value
    inputDiscountValue.value = ""
})

// handle chose category when click button
const btnChoseCategory = document.getElementById('btn-chose-category')
btnChoseCategory.addEventListener('click', () => {
    const categorySelected = $('#category').val().map(id => Number(id));

    const products = productList.filter(product => {
        return categorySelected.includes(product.category.categoryId)
    })

    // remove duplicate product
    const productIds = productSelected.map(product => product.productId)
    const newProducts = products.filter(product => !productIds.includes(product.productId)).map(product => {
        return {...product, isChecked: false}
    })
    productSelected = [...productSelected, ...newProducts]

    renderProducts(productSelected)
    $('#modal-category').modal('hide')
})

// handle check all product when click checkbox
const inputCheckAll = document.getElementById('input-check-all')
inputCheckAll.addEventListener('change', (e) => {
    if (e.target.checked) {
        productSelected = productSelected.map(product => {
            return {...product, isChecked: true}
        })
    } else {
        productSelected = productSelected.map(product => {
            return {...product, isChecked: false}
        })
    }
    btnDeleteAllProduct.disabled = !checkEnableBtnDeleteAllProduct();
    renderProducts(productSelected)
})

// handle change checkbox when click checkbox
const selectProductDelete = (productId) => {
    productSelected = productSelected.map(product => {
        if (product.productId === productId) {
            return {...product, isChecked: !product.isChecked}
        }
        return product
    })
    inputCheckAll.checked = productSelected.every(product => product.isChecked)
    btnDeleteAllProduct.disabled = !checkEnableBtnDeleteAllProduct();
    renderProducts(productSelected)
}

// handle create discount campaign when click button using vanilla javascript and axios
const btnUpdate = document.getElementById('btn-update')
btnUpdate.addEventListener('click', () => {
    if (!$('#form-update-discount-campaing').valid()) {
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
        endDate: new Date(endDate),
        productIds: productSelected.map(product => product.productId),
    }
    console.log(data)

    axios.put(`/api/v1/admin/discount-campaigns/${discountCampaign.campaignId}`, data)
        .then(res => {
            if (res.status === 200) {
                toastr.success('Cập nhật khuyến mại thành công')
            } else {
                toastr.error('Cập nhật khuyến mại thất bại')
            }
        })
        .catch(err => {
            console.log(err)
            toastr.error(err.response.data.message)
        })
})

renderProducts(productSelected)