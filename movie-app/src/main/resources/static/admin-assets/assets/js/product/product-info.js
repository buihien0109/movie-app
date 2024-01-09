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
$('#form-update-product').validate({
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

// --------- Product Info ---------
// render sub category
const renderSubCategory = (parentId) => {
    const subCategoryList = categoryList.find(category => category.mainCategory.categoryId === Number(parentId)).subCategories;
    subCategory.innerHTML = ""
    let html = '<option hidden="hidden">Chọn danh mục con</option>';
    subCategoryList.forEach(subCategory => {
        html += `<option value="${subCategory.categoryId}">${subCategory.name}</option>`
    });
    subCategory.innerHTML = html;
}

// triger value parent category, sub category
const parentCategory = document.getElementById('parent-category');
const subCategory = document.getElementById('sub-category');
parentCategory.value = product.category.parentCategoryId;
renderSubCategory(product.category.parentCategoryId);
subCategory.value = product.category.categoryId;

// format price
const price = document.getElementById('price');
price.value = product.price.toString().replace(/\D/g, "").replace(/\B(?=(\d{3})+(?!\d))/g, ",");

// Handle event change parent category and load sub category using vanilla js
parentCategory.addEventListener('change', function (event) {
    const categoryId = event.target.value;
    renderSubCategory(Number(categoryId));
});

// format price when user input
price.addEventListener('keyup', function (event) {
    const value = event.target.value;
    event.target.value = value.replace(/\D/g, "").replace(/\B(?=(\d{3})+(?!\d))/g, ",");
});

// handle event submit form
const btnUpdate = document.getElementById('btn-update');
btnUpdate.addEventListener(("click"), () => {
    if (!$('#form-update-product').valid()) {
        return;
    }

    // send request to server
    const name = document.getElementById('name').value;
    const price = formatPrice(document.getElementById('price').value);
    const description = document.getElementById('description').value;
    const status = document.getElementById('status').value;
    const categoryId = Number(document.getElementById('sub-category').value);
    const supplierId = Number(document.getElementById('supplier').value);
    const attributes = product.attributes;

    // create product object
    const data = {
        name, price, description, status, categoryId, supplierId, attributes
    }

    axios.put(`/api/v1/admin/products/${product.productId}`, data)
        .then(response => {
            if (response.status === 200) {
                toastr.success("Cập nhật sản phẩm thành công");
            } else {
                toastr.error("Cập nhật sản phẩm thất bại");
            }
        })
        .catch(error => {
            console.log(error);
            toastr.error(error.response.data.message);
        })
})