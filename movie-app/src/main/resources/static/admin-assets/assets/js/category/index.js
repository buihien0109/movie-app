let idParentUpdate = null;
let idSubUpdate = null;

// validate
$('#form-parent-category').validate({
    rules: {
        parentName: {
            required: true
        }
    },
    messages: {
        parentName: {
            required: "Tên danh mục cha không được để trống"
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

$.validator.addMethod(
    "userRole",
    function (value, element) {
        const userRole = roles.find(e => e.name === "USER")
        const rolesSelected = $("#roles").val().map(e => Number(e))
        return rolesSelected.includes(userRole.roleId);
    },
    "Role USER phải được chọn"
);

$('#form-sub-category').validate({
    rules: {
        parentId: {
            required: true
        },
        subName: {
            required: true
        }
    },
    messages: {
        parentId: {
            required: "Danh mục cha không được để trống"
        },
        subName: {
            required: "Tên danh mục con không được để trống"
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


// Render tag
const tableContent = document.querySelector("table tbody")
const renderCategoryList = (data) => {
    let html = "";
    data.forEach((item) => {
        html += `
                        <tr>
                            <td rowspan="${item.subCategories.length + 1}">
                                <div class="d-flex justify-content-between align-items-center">
                                    <span>${item.mainCategory.name}</span>
                                    <div>
                                        <span class="btn-action btn-action-edit" onclick="openModalUpdateParent(${item.mainCategory.categoryId})"><i class="fas fa-pencil-alt"></i></span>
                                        <span class="btn-action btn-action-delete" onclick="deleteCategory(${item.mainCategory.categoryId}, 'main')"><i class="fas fa-trash-alt"></i></span>
                                    </div>
                                </div>
                            </td>
                        </tr>
                `;
        item.subCategories.forEach((subItem) => {
            html += `
                        <tr>
                            <td>
                                <div class="d-flex justify-content-between align-items-center">
                                    <span>${subItem.name}</span>
                                    <div>
                                        <span class="btn-action btn-action-edit" onclick="openModalUpdateSub(${subItem.categoryId})"><i class="fas fa-pencil-alt"></i></span>
                                        <span class="btn-action btn-action-delete" onclick="deleteCategory(${subItem.categoryId}, 'sub')"><i class="fas fa-trash-alt"></i></span>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    `;
        });
    });
    tableContent.innerHTML = html;
};

// render pagination using pagination.js
const renderPagination = (categories) => {
    $('#pagination').pagination({
        dataSource: categories,
        className: 'paginationjs-theme-blue paginationjs-big',
        callback: function (data) {
            renderCategoryList(data);
        },
        hideOnlyOnePage: true
    })
}

// handle delete category using axios
const deleteCategory = (categoryId, type) => {
    const isDelete = confirm("Bạn có chắc chắn muốn xóa danh mục này không?");
    if (!isDelete) return;
    axios.delete(`/api/v1/admin/categories/${categoryId}`)
        .then((response) => {
            if (response.status === 200) {
                toastr.success("Xóa thành công");

                // if type = 'main' => delete main category
                if (type === 'main') {
                    categoryList = categoryList.filter((item) => {
                        return item.mainCategory.categoryId !== categoryId;
                    });
                } else {
                    // if type = 'sub' => delete sub category
                    categoryList.forEach((item) => {
                        item.subCategories = item.subCategories.filter((subItem) => {
                            return subItem.categoryId !== categoryId;
                        });
                    });
                }

                renderPagination(categoryList);

            } else {
                toastr.error("Xóa thất bại");
            }
        })
        .catch((error) => {
            console.log(error);
            toastr.error(error.response.data.message);
        })
}

// ------------ PARENT CATEGORY ------------
// Open modal create
const inputParentNameEl = document.getElementById("input-parent-name");
const btnOpenModalParent = document.getElementById("btn-open-modal-parent");
btnOpenModalParent.addEventListener("click", () => {
    $('#modal-parent-category').modal('show');

    // set title modal
    const modalParentTitle = document.getElementById("modal-parent-title")
    modalParentTitle.innerText = "Tạo danh mục cha"
})

$('#modal-parent-category').on('hidden.bs.modal', function (event) {
    inputParentNameEl.value = "";
    idParentUpdate = null;
})

// Create tag using axios and vanilla js
const createParentCategory = () => {
    if (!$('#form-parent-category').valid()) return;

    const name = inputParentNameEl.value;
    if (!name || name.trim() === "") {
        toastr.error("Tên danh mục không được để trống")
        return
    }

    axios.post("/api/v1/admin/categories/add-parent", {name})
        .then(res => {
            categoryList.push(res.data)
            renderPagination(categoryList)
            inputParentNameEl.value = "";

            $('#modal-parent-category').modal('hide');
            toastr.success("Tạo thành công")
        })
        .catch(e => {
            toastr.error(e.response.data.message);
            console.log(e);
        })
}

// Update tag using axios and vanilla js
const updateParentCategory = () => {
    if (!$('#form-parent-category').valid()) return;

    const name = inputParentNameEl.value;
    if (!name || name.trim() === "") {
        toastr.error("Tên danh mục không được để trống")
        return
    }

    axios.put(`/api/v1/admin/categories/${idParentUpdate}/update-parent`, {name})
        .then(res => {
            const category = categoryList.find(category => category.mainCategory.categoryId === idParentUpdate).mainCategory;
            category.name = res.data.name;

            renderPagination(categoryList)
            inputParentNameEl.value = "";

            $('#modal-parent-category').modal('hide');
            toastr.success("Cập nhật thành công");
            idParentUpdate = null;
        })
        .catch(e => {
            toastr.error(e.response.data.message);
            console.log(e);
        })
}

// Handle open modal update
const openModalUpdateParent = id => {
    const category = categoryList.find(category => category.mainCategory.categoryId === id).mainCategory;
    console.log(category);
    inputParentNameEl.value = category.name
    idParentUpdate = category.categoryId;
    $('#modal-parent-category').modal('show');

    // set title modal
    const modalTitle = document.getElementById("modal-parent-title")
    modalTitle.innerText = "Cập nhật danh mục cha"
}

// Handle tag
const btnHandleParent = document.getElementById("btn-handle-parent")
btnHandleParent.addEventListener("click", () => {
    if (idParentUpdate) {
        updateParentCategory()
    } else {
        createParentCategory()
    }
})

// ------------ SUB CATEGORY ------------
// Open modal create
const inputSubNameEl = document.getElementById("input-sub-name");
const inputParentIdEl = document.getElementById("input-parent-id");
const btnOpenModalSub = document.getElementById("btn-open-modal-sub");
btnOpenModalSub.addEventListener("click", () => {
    $('#modal-sub-category').modal('show');

    // set title modal
    const modalSubTitle = document.getElementById("modal-sub-title")
    modalSubTitle.innerText = "Tạo danh mục con"
})

$('#modal-sub-category').on('hidden.bs.modal', function (event) {
    inputSubNameEl.value = "";
    inputParentIdEl.value = "";
    idSubUpdate = null;
})

// Create tag using axios and vanilla js
const createSubCategory = () => {
    if (!$('#form-sub-category').valid()) return;

    const parentCategoryId = inputParentIdEl.value;
    const name = inputSubNameEl.value;
    if (!name || name.trim() === "") {
        toastr.error("Tên danh mục không được để trống")
        return
    }

    const data = {
        name,
        parentCategoryId: Number(parentCategoryId)
    }
    console.log(data)

    axios.post("/api/v1/admin/categories/add-sub", data)
        .then(res => {
            // find main category
            const category = categoryList.find(category => category.mainCategory.categoryId === res.data.parentCategoryId);
            // push sub category to sub category
            category.subCategories.push(res.data);

            renderPagination(categoryList)
            inputSubNameEl.value = "";
            inputParentIdEl.value = "";

            $('#modal-sub-category').modal('hide');
            toastr.success("Tạo thành công")
        })
        .catch(e => {
            toastr.error(e.response.data.message);
            console.log(e);
        })
}

// Update tag using axios and vanilla js
const updateSubCategory = () => {
    if (!$('#form-sub-category').valid()) return;

    const parentCategoryId = inputParentIdEl.value;
    const name = inputSubNameEl.value;
    if (!name || name.trim() === "") {
        toastr.error("Tên danh mục không được để trống")
        return
    }

    const data = {
        name,
        parentCategoryId: Number(parentCategoryId)
    }

    axios.put(`/api/v1/admin/categories/${idSubUpdate}/update-sub`, data)
        .then(res => {
            // find sub category in sub category list
            const subCategory = categoryList.find(category => category.subCategories.find(subCategory => subCategory.categoryId === idSubUpdate)).subCategories.find(subCategory => subCategory.categoryId === idSubUpdate);
            if (subCategory.parentCategoryId === res.data.parentCategoryId) {
                subCategory.name = res.data.name;
                subCategory.slug = res.data.slug;
            } else {
                // remove sub category in sub category list
                categoryList.forEach(category => {
                    category.subCategories = category.subCategories.filter(subCategory => subCategory.categoryId !== idSubUpdate)
                })

                // find main category
                const category = categoryList.find(category => category.mainCategory.categoryId === res.data.parentCategoryId);
                // push sub category to sub category
                category.subCategories.push(res.data);
            }

            console.log(categoryList)
            renderPagination(categoryList)
            inputSubNameEl.value = "";
            inputParentIdEl.value = "";

            $('#modal-sub-category').modal('hide');
            toastr.success("Cập nhật thành công");
            idSubUpdate = null;
        })
        .catch(e => {
            toastr.error(e.response.data.message);
            console.log(e);
        })
}

// Handle open modal update
const openModalUpdateSub = id => {
    // find sub category
    const subCategory = categoryList.find(category => category.subCategories.find(subCategory => subCategory.categoryId === id)).subCategories.find(subCategory => subCategory.categoryId === id);

    inputSubNameEl.value = subCategory.name
    inputParentIdEl.value = subCategory.parentCategoryId
    idSubUpdate = subCategory.categoryId;
    $('#modal-sub-category').modal('show');

    // set title modal
    const modalTitle = document.getElementById("modal-sub-title")
    modalTitle.innerText = "Cập nhật danh mục con"
}

// Handle tag
const btnHandleSub = document.getElementById("btn-handle-sub")
btnHandleSub.addEventListener("click", () => {
    if (idSubUpdate) {
        updateSubCategory()
    } else {
        createSubCategory()
    }
})


renderPagination(categoryList);