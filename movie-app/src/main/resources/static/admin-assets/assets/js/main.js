$(window).scroll(function () {
    let scroll = $(window).scrollTop();
    let box = $('.header-text').height();
    let header = $('header').height();

    if (scroll >= box - header) {
        $("header").addClass("background-header");
    } else {
        $("header").removeClass("background-header");
    }
});

// Active menu
const activeMenu = () => {
    const menuParentEls = document.querySelectorAll("#main-menu > li > a");
    const path = window.location.pathname;

    menuParentEls.forEach(menuParent => {
        const menuChildEls = menuParent.nextElementSibling.querySelectorAll("ul.nav.nav-treeview > li > a");
        menuChildEls.forEach(menuChild => {
            if (menuChild.getAttribute("href") === path) {
                menuParent.parentElement.classList.add("menu-is-opening", "menu-open");
                menuParent.classList.add("active");
                menuChild.classList.add("active");
            }
        })
    })
}
activeMenu();

// show error message
const showError = (message) => {
    // check if error is string or object
    if (typeof message === 'string') {
        toastr.error(message);
    } else {
        // for loop in object and show all value
        for (const key in message) {
            toastr.error(message[key]);
        }
    }
};

/*------------------
toastr config
--------------------*/
toastr.options = {
    "closeButton": false,
    "debug": false,
    "newestOnTop": false,
    "progressBar": false,
    "positionClass": "toast-top-right",
    "preventDuplicates": false,
    "onclick": null,
    "showDuration": "300",
    "hideDuration": "1000",
    "timeOut": "2000",
    "extendedTimeOut": "1000",
    "showEasing": "swing",
    "hideEasing": "linear",
    "showMethod": "fadeIn",
    "hideMethod": "fadeOut"
}

const formatDateTime = datetimeString => {
    const date = new Date(datetimeString);
    const day = `0${date.getDate()}`.slice(-2);
    const month = `0${date.getMonth() + 1}`.slice(-2);
    const year = date.getFullYear();
    const hour = `0${date.getHours()}`.slice(-2);
    const minute = `0${date.getMinutes()}`.slice(-2);
    const second = `0${date.getSeconds()}`.slice(-2);
    return `${hour}:${minute}:${second} - ${day}/${month}/${year}`;
}

const formatDate = dateString => {
    const date = new Date(dateString);
    const day = `0${date.getDate()}`.slice(-2);
    const month = `0${date.getMonth() + 1}`.slice(-2);
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
}

const formatCurrency = (number) => {
    if (number) {
        const numberFormat = number.toString()
            .replace(/\D/g, "").replace(/\B(?=(\d{3})+(?!\d))/g, ",");
        return number >= 0 ? numberFormat : `-${numberFormat}`;
    }
    return number;
}

// handle logout
function logout() {
    axios.post('/logout').then(res => {
        if (res.status === 200) {
            toastr.success("Đăng xuất thành công");
            setTimeout(() => {
                window.location.href = '/dang-nhap';
            }, 1500)
        }
    }).catch(err => {
        console.log(err);
        showError(err.response.data.message);
    })
}
