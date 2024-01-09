let idTagUpdate = null;

// validate form
$('#form-tag').validate({
    rules: {
        name: {
            required: true
        }
    },
    messages: {
        name: {
            required: "Tên banner không được để trống"
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
const renderTags = (tagList, pagination) => {
    console.log(pagination)
    tableContent.innerHTML = "";
    let html = "";
    tagList.forEach((tag, index) => {
        html += `
                    <tr>
                        <td>${pagination.pageSize * (pagination.pageNumber - 1) + (index + 1)}</td>
                        <td>${tag.name}</td>
                        <td>
                            <span class="mr-2">${tag.used}</span>
                            ${tag.used > 0 ? `(<a href="javascript:void(0)" onclick="showBlogList(${tag.id})">Xem danh sách</a>)` : ''}
                        </td>
                        <td>
                            <button class="btn btn-warning" onclick="openModalUpdate(${tag.id})">Edit</button>
                            <button class="btn btn-danger" onclick="deletetag(${tag.id})">Delete</button>
                        </td>
                    </tr>
                `
    })
    tableContent.innerHTML = html;
}

// render pagination using pagination.js
const renderPagination = (tagList) => {
    $('#pagination').pagination({
        dataSource: tagList,
        className: 'paginationjs-theme-blue paginationjs-big',
        callback: function (data, pagination) {
            renderTags(data, pagination);
        },
        hideOnlyOnePage: true
    })
}

// Open modal create
const inputNameEl = document.getElementById("input-name");
const btnOpenModal = document.getElementById("btn-open-modal");
btnOpenModal.addEventListener("click", () => {
    $('#modal-tag').modal('show');

    // set title modal
    const modalTitle = document.getElementById("modal-tag-title")
    modalTitle.innerText = "Tạo tag"
})

$('#modal-tag').on('hidden.bs.modal', function (event) {
    inputNameEl.value = "";
    idTagUpdate = null;
})

// Create tag using axios and vanilla js
const createTag = () => {
    if (!$('#form-tag').valid()) {
        return;
    }

    const name = inputNameEl.value;
    if (!name || name.trim() === "") {
        toastr.error("Tên tag không được để trống")
        return
    }

    axios.post("/api/v1/admin/tags", {name})
        .then(res => {
            tags.push(res.data)
            renderPagination(tags)
            inputNameEl.value = "";

            $('#modal-tag').modal('hide');
            toastr.success("Tạo thành công")
        })
        .catch(e => {
            toastr.error(e.response.data.message);
            console.log(e);
        })
}

// Update tag using axios and vanilla js
const updateTag = () => {
    if (!$('#form-tag').valid()) {
        return;
    }

    const name = inputNameEl.value;
    if (!name || name.trim() === "") {
        toastr.error("Tên tag không được để trống")
        return
    }

    axios.put(`/api/v1/admin/tags/${idTagUpdate}`, {name})
        .then(res => {
            const tag = tags.find(tag => tag.id === idTagUpdate)
            tag.name = res.data.name;

            renderPagination(tags)
            inputNameEl.value = "";

            $('#modal-tag').modal('hide');
            toastr.success("Cập nhật thành công");
            idTagUpdate = null;
        })
        .catch(e => {
            toastr.error(e.response.data.message);
            console.log(e);
        })
}

// Handle open modal update
const openModalUpdate = id => {
    const tag = tags.find(tag => tag.id === id)
    inputNameEl.value = tag.name
    idTagUpdate = tag.id;
    $('#modal-tag').modal('show');

    // set title modal
    const modalTitle = document.getElementById("modal-tag-title")
    modalTitle.innerText = "Cập nhật tag"
}

// Handle tag
const btnHandleTag = document.getElementById("btn-handle-tag")
btnHandleTag.addEventListener("click", () => {
    if (idTagUpdate) {
        updateTag()
    } else {
        createTag()
    }
})

// Xóa
const deletetag = (id) => {
    const isDelete = confirm("Bạn có chắc chắn muốn xóa tag này không?")
    if (isDelete) {
        axios.delete(`/api/v1/admin/tags/${id}`)
            .then(() => {
                tags = tags.filter(tag => tag.id !== id)
                renderPagination(tags)
                toastr.success("Xóa thành công")
            })
            .catch(e => {
                toastr.error(e.response.data.message);
                console.log(e);
            })
    }
}

// call api get blog by tag id and show blog list in modal
const showBlogList = (id) => {
    axios.get(`/api/v1/admin/tags/${id}/blogs`)
        .then(res => {
            console.log(res)
            const blogList = res.data;
            let html = "";
            blogList.forEach((blog, index) => {
                html += `
                            <tr>
                                <td>${index + 1}</td>
                               <td>
                                    <a href="/admin/blogs/${blog.id}/detail">${blog.title}</a>
                                </td>
                                <td>
                                    <a href="/admin/users/${blog.user.userId}">${blog.user.username}</a>
                                </td>
                                <td>${formatDate(blog.createdAt)}</td>
                            </tr>
                        `
            })
            document.getElementById("modal-blog-list-body").innerHTML = html;

            // update modal title
            const modalTitle = document.querySelector("#modal-blog-list .modal-title")
            const tag = tags.find(tag => tag.id === id)
            modalTitle.innerHTML = `Danh sách bài viết sử dụng tag <span class="text-primary">${tag.name}</span>`
            $('#modal-blog-list').modal('show');
        })
        .catch(e => {
            toastr.error(e.response.data.message);
            console.log(e);
        })
}

renderPagination(tags);