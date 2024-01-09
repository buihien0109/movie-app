$('#form-create-banner').validate({
    rules: {
        name: {
            required: true
        },
        linkRedirect: {
            required: true
        }
    },
    messages: {
        name: {
            required: "Tên banner không được để trống"
        },
        linkRedirect: {
            required: "Link không được để trống"
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

// send request to server when click button create using axios
const btnCreate = document.getElementById('btn-create');
btnCreate.addEventListener('click', function () {
    if (!$('#form-create-banner').valid()) return;

    const name = document.getElementById('name').value;
    const linkRedirect = document.getElementById('link-redirect').value;
    const status = document.getElementById('status').value;
    const url = document.getElementById('image').src;

    const data = {
        name: name,
        linkRedirect: linkRedirect,
        status: status === "true",
        url: url
    };

    console.log(data)

    axios.post('/api/v1/admin/banners', data)
        .then(function (response) {
            if (response.status === 200) {
                toastr.success('Tạo banner thành công');
                setTimeout(function () {
                    window.location.href = `/admin/banners/${response.data.id}/detail`;
                }, 1500);
            } else {
                toastr.error('Tạo banner thất bại');
            }
        })
        .catch(function (error) {
            console.log(error);
            toastr.error(error.response.data.message);
        });
});