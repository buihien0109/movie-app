// convert price to number when user submit
const formatPrice = (price) => {
    return parseInt(price.replace(/,/g, ""));
}

// validate form
$.validator.addMethod(
    "validateMinValue",
    function (value, element) {
        // get value of type
        return formatPrice(value) > 0
    },
    "Giá trị giảm phải lớn hơn 0"
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
$('#form-create-product').validate({
    rules: {
        name: {
            required: true
        },
        description: {
            required: true
        },
        price: {
            required: true,
            validateMinValue: true,
            validateInteger: true
        },
        status: {
            required: true
        },
        parent: {
            required: true,
            validateInteger: true
        },
        sub: {
            required: true,
            validateInteger: true
        }
    },
    messages: {
        name: {
            required: "Tên chiến dịch giảm giá không được để trống"
        },
        description: {
            required: "Mô tả không được để trống",
        },
        price: {
            required: "Giá không được để trống",
            validateMinValue: "Giá phải lớn hơn 0",
            validateInteger: "Giá phải là số nguyên"
        },
        status: {
            required: "Trạng thái không được để trống"
        },
        parent: {
            required: "Danh mục cha không được để trống",
            validateInteger: "Danh mục cha không được để trống"
        },
        sub: {
            required: "Danh mục con không được để trống",
            validateInteger: "Danh mục con không được để trống"
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

// Handle event change parent category and load sub category using vanilla js
const parentCategory = document.getElementById('parent-category');
const subCategory = document.getElementById('sub-category');

parentCategory.addEventListener('change', function (event) {
    const categoryId = event.target.value;
    const subCategoryList = categoryList.find(category => category.mainCategory.categoryId === Number(categoryId)).subCategories;
    subCategory.innerHTML = ""
    let html = '<option hidden="hidden">Chọn danh mục con</option>';
    subCategoryList.forEach(subCategory => {
        html += `<option value="${subCategory.categoryId}">${subCategory.name}</option>`
    });
    subCategory.innerHTML = html;
    subCategory.disabled = false;
});

// format price when user input
const price = document.getElementById('price');
price.addEventListener('keyup', function (event) {
    const value = event.target.value;
    event.target.value = value.replace(/\D/g, "").replace(/\B(?=(\d{3})+(?!\d))/g, ",");
});

// Handle event click button create product. Then send request to server using axios
const btnCreate = document.getElementById('btn-create');

btnCreate.addEventListener(("click"), () => {
    const categoryId = Number(document.getElementById('sub-category').value);
    const pcategoryId = Number(document.getElementById('parent-category').value);
    console.log(categoryId, pcategoryId)
    if (!$('#form-create-product').valid()) {
        return;
    }

    // send request to server
    const name = document.getElementById('name').value;
    const price = formatPrice(document.getElementById('price').value);
    const description = document.getElementById('description').value;
    const status = document.getElementById('status').value;
    // const categoryId = Number(document.getElementById('sub-category').value);

    // create product object
    const data = {
        name, price, description, status, categoryId
    }

    axios.post('/api/v1/admin/products', data)
        .then(response => {
            console.log(response)
            toastr.success('Tạo sản phẩm thành công');
            setTimeout(() => {
                window.location.href = `/admin/products/${response.data.productId}/detail`;
            }, 1500)
        })
        .catch(error => {
            console.log(error);
            toastr.error(error.response.data.message);
        })
})
