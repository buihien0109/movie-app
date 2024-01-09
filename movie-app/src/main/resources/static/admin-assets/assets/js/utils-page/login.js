// hide/show password with icon in inputs using valina javascript
const togglePasswordBtns = document.querySelectorAll('.icon-toggle-password');
const passwordInputs = document.querySelectorAll('input[type="password"]');
togglePasswordBtns.forEach((btn, index) => {
    btn.addEventListener('click', () => {
        if (passwordInputs[index].type === 'password') {
            passwordInputs[index].type = 'text';
            btn.innerHTML = '<i class="fa fa-eye-slash" aria-hidden="true"></i>';
        } else {
            passwordInputs[index].type = 'password';
            btn.innerHTML = '<i class="fa fa-eye" aria-hidden="true"></i>';
        }
    });
});

// validation form
$('#form-login-admin').validate({
    rules: {
        email: {
            required: true,
            email: true
        },
        password: {
            required: true
        }
    },
    messages: {
        email: {
            required: "Email không được để trống",
            email: "Email không đúng định dạng"
        },
        password: {
            required: "Mật khẩu không được để trống",
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

// handle login form submit using axios
const loginForm = document.getElementById('form-login-admin');
loginForm.addEventListener('submit', (e) => {
    e.preventDefault();
    if (!$('#form-login-admin').valid()) {
        return;
    }

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const data = {
        email,
        password
    };
    console.log({data});
    axios.post('/api/v1/admin/auth/login', data)
        .then((res) => {
            if (res.status === 200) {
                toastr.success("Đăng nhập thành công");
                setTimeout(() => {
                    window.location.href = '/admin/dashboard';
                }, 1500);
            } else {
                toastr.error("Đăng nhập thất bại");
            }
        })
        .catch((err) => {
            console.log(err);
            toastr.error(err.response.data.message);
        });
});