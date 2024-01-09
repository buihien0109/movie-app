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


$('#form-create-blog').validate({
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
    },
    unhighlight: function (element, errorClass, validClass) {
        $(element).removeClass('is-invalid');
    }
});

// Truy cập
const titleEl = document.getElementById("title");
const descriptionEl = document.getElementById("description");
const statusEl = document.getElementById("status");
const tagEl = $("#tag");
const btnCreate = document.getElementById("btn-create");

// create blog using axios and promise
btnCreate.addEventListener("click", () => {
    if (!$('#form-create-blog').valid()) {
        return;
    }

    const data = {
        title: titleEl.value,
        content: $('#content').summernote('code'),
        description: descriptionEl.value,
        status: statusEl.value === "true",
        tagIds: tagEl.val().map(id => Number(id))
    };
    console.log(data)

    axios.post("/api/v1/admin/blogs", data)
        .then((res) => {
            toastr.success("Tạo bài viết thành công");
            setTimeout(() => {
                window.location.href = `/admin/blogs/${res.data.id}/detail`
            }, 1500);
        })
        .catch((err) => {
            console.log(err);
            toastr.error(err.response.data.message);
        });
});