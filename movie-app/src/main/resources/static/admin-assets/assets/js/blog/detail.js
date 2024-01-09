//Initialize Select2 Elements
$(".select2").select2({
    placeholder: "Chọn danh mục",
});

$('#content').summernote({
    height: 500,
})

$('#content').css({
    'display': 'block',
    'height': '0',
    'padding': '0',
    'border': 'none'
})

$('#content').on('summernote.change', function(we, contents, $editable) {
    $("#content").html(contents);
});

// Initialize data
const initData = (data) => {
    $("#title").val(data.title);
    $('#content').summernote('code', data.content);
    $("#description").html(data.description);
    $("#status").val(String(data.status));
    $("#tag").val(data.tags.map(e => e.id)).trigger("change");
    $("#image").attr("src", data.thumbnail ? data.thumbnail : "https://placehold.co/1000x600");
};
initData(blog);

$('#form-update-blog').validate({
    rules: {
        title: {
            required: true
        },
        content: {
            required: true
        },
        description: {
            required: true
        }
    },
    messages: {
        title: {
            required: "Tiêu đề không được để trống"
        },
        content: {
            required: "Nội dung không được để trống"
        },
        description: {
            required: "Mô tả không được để trống"
        }
    },
    errorElement: 'span',
    errorPlacement: function (error, element) {
        error.addClass('invalid-feedback');
        element.closest('.form-group').append(error);
    },
    highlight: function (element, errorClass, validClass) {
        $(element).addClass('is-invalid');
        console.log(element)
    },
    unhighlight: function (element, errorClass, validClass) {
        $(element).removeClass('is-invalid');
    }
});

// Handle event update blog and send request to server using axios
const btnUpdate = document.getElementById("btn-update");
btnUpdate.addEventListener("click", () => {
    if (!$('#form-update-blog').valid()) {
        return;
    }

    const title = $("#title").val();
    const content = $('#content').summernote('code');
    const description = $("#description").val();
    const status = $("#status").val();
    const tagIds = $("#tag").val();
    const thumbnail = $("#image").attr("src");

    const data = {
        title,
        content,
        description,
        status: status === "true",
        tagIds: tagIds.map(e => Number(e)),
        thumbnail,
    };
    console.log(data)

    axios.put(`/api/v1/admin/blogs/${blog.id}`, data)
        .then(res => {
            toastr.success("Cập nhật thành công");
        })
        .catch(err => {
            console.log(err);
            toastr.error("Cập nhật thất bại");
        });
});

// Handle event delete blog and send request to server using axios and confirm before delete using window.confirm
const btnDelete = document.getElementById("btn-delete");
btnDelete.addEventListener("click", () => {
    if (window.confirm("Bạn có chắc chắn muốn xóa bài viết này?")) {
        axios.delete(`/api/v1/admin/blogs/${blog.id}`)
            .then(res => {
                toastr.success("Xóa thành công");
                setTimeout(() => {
                    window.location.href = "/admin/blogs/own-blogs";
                }, 1500);
            })
            .catch(err => {
                console.log(err);
                toastr.error("Xóa thất bại");
            });
    }
});