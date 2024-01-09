let idUpdate = null;

// validate
$.validator.addMethod(
    "userRole",
    function (value, element) {
        const userRole = roles.find(e => e.name === "USER")
        const rolesSelected = $("#roles").val().map(e => Number(e))
        return rolesSelected.includes(userRole.roleId);
    },
    "Role USER phải được chọn"
);

$('#form-coupon').validate({
    rules: {
        code: {
            required: true
        },
        discount: {
            required: true,
            min: 0,
            max: 100
        }
    },
    messages: {
        code: {
            required: "Coupon code không được để trống"
        },
        discount: {
            required: "Giá trị % giảm không được để trống",
            min: "Giá trị % giảm phải lớn hơn 0",
            max: "Giá trị % giảm phải nhỏ hơn 100"
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


// init date range picker
$('#date').daterangepicker()

// Compare 2 date string
const compareDate = (date1, date2) => {
    const d1 = new Date(date1);
    const d2 = new Date(date2);
    return d1.getTime() - d2.getTime();
}

// Generate status discount
const generateStatus = (validFrom, validTo) => {
    const now = new Date();
    const from = new Date(validFrom);
    const to = new Date(validTo);
    if (compareDate(now, from) < 0) {
        return `<span class="badge badge-warning">Chưa kích hoạt</span>`;
    } else if (compareDate(now, to) > 0) {
        return `<span class="badge badge-secondary">Đã hết hạn</span>`;
    } else {
        return `<span class="badge badge-success">Đang kích hoạt</span>`;
    }
}

// Render tag
const tableContent = document.querySelector("table tbody")
const renderCoupons = (couponList) => {
    tableContent.innerHTML = "";
    let html = "";
    couponList.forEach((coupon) => {
        html += `
                    <tr>
                        <td>${coupon.couponId}</td>
                        <td>${coupon.code}</td>
                        <td>${coupon.discount}%</td>
                        <td>${generateStatus(coupon.validFrom, coupon.validTo)}</td>
                        <td>${formatDate(coupon.validFrom)} - ${formatDate(coupon.validTo)}</td>
                        <td>
                            <button class="btn btn-primary btn-sm btn-delete" onclick="openModalUpdateCoupon(${coupon.couponId})">
                                <i class="fas fa-pen"></i>
                            </button>
                            <button class="btn btn-danger btn-sm btn-delete" onclick="deleteCoupon(${coupon.couponId})">
                                <i class="fas fa-trash"></i>
                            </button>
                        </td>
                    </tr>
                `
    })
    tableContent.innerHTML = html;
}

// render pagination using pagination.js
const renderPagination = () => {
    $('#pagination').pagination({
        dataSource: coupons,
        className: 'paginationjs-theme-blue paginationjs-big',
        callback: function (data, pagination) {
            renderCoupons(data);
        },
        hideOnlyOnePage: true
    })
}

// Handle open modal create coupon
const btnOpenModal = document.getElementById("btn-open-modal");
btnOpenModal.addEventListener("click", () => {
    // change modal title
    const modalTitle = document.querySelector(".modal-title");
    modalTitle.innerHTML = "Tạo coupon";

    $('#modal-coupon').modal('show');
})

const openModalUpdateCoupon = id => {
    // change modal title
    const modalTitle = document.querySelector(".modal-title");
    modalTitle.innerHTML = "Cập nhật coupon";

    // get coupon by id
    const coupon = coupons.find(coupon => coupon.couponId === id);

    // set value for input
    const code = document.getElementById("code");
    const discount = document.getElementById("discount");

    code.value = coupon.code;
    discount.value = coupon.discount;

    // set date for daterangepicker
    $('#date').daterangepicker({
        startDate: moment(coupon.validFrom, "YYYY-MM-DD"), // Set your desired start date
        endDate: moment(coupon.validTo, "YYYY-MM-DD"), // Set your desired end date
    })

    // set id for update
    idUpdate = id;

    $('#modal-coupon').modal('show');
}

// Handle hidden modal
$('#modal-coupon').on('hidden.bs.modal', function (e) {
    // reset value for input
    const code = document.getElementById("code");
    const discount = document.getElementById("discount");

    code.value = "";
    discount.value = "";

    // reset date for daterangepicker
    $("#date").data('daterangepicker').setStartDate(moment().format("MM-DD-YYYY"));
    $("#date").data('daterangepicker').setEndDate(moment().format("MM-DD-YYYY"));

    // reset id for update
    idUpdate = null;
})

// Handle create coupon
const createCoupon = () => {
    if (!$('#form-coupon').valid()) return;

    const code = document.getElementById("code").value;
    const discount = document.getElementById("discount").value;
    const date = document.getElementById("date").value;

    console.log(date)
    const [validFrom, validTo] = date.split(" - ");
    console.log({validFrom, validTo})

    const coupon = {
        code,
        discount,
        validFrom: new Date(validFrom),
        validTo: new Date(validTo)
    }
    console.log(coupon)

    // send request to server using axios
    axios.post("/api/v1/admin/coupons", coupon)
        .then(res => {
            // add new coupon to list
            coupons.push(res.data);

            // render pagination
            renderPagination();

            // hidden modal
            $('#modal-coupon').modal('hide');

            toastr.success("Tạo coupon thành công");
        })
        .catch(err => {
            console.log(err);
            toastr.error(err.response.data.message);
        })
}

const updateCoupon = () => {
    if (!$('#form-coupon').valid()) return;

    // get value from input
    const code = document.getElementById("code").value;
    const discount = document.getElementById("discount").value;
    const date = document.getElementById("date").value;

    const [validFrom, validTo] = date.split(" - ");
    const coupon = {
        code,
        discount,
        validFrom: new Date(validFrom),
        validTo: new Date(validTo)
    }

    // send request to server using axios
    axios.put(`/api/v1/admin/coupons/${idUpdate}`, coupon)
        .then(res => {
            // update coupon in list
            const index = coupons.findIndex(coupon => coupon.couponId === idUpdate);
            coupons[index] = res.data;

            // render pagination
            renderPagination();

            // hidden modal
            $('#modal-coupon').modal('hide');

            toastr.success("Cập nhật coupon thành công");
        })
        .catch(err => {
            console.log(err);
            toastr.error(err.response.data.message);
        })
}

const btnHandle = document.getElementById("btn-handle");
btnHandle.addEventListener("click", () => {
    if (idUpdate) {
        updateCoupon();
    } else {
        createCoupon();
    }
})

const deleteCoupon = id => {
    const isDelete = confirm("Bạn có chắc chắn muốn xóa coupon này?");
    if (!isDelete) return;

    // send request to server using axios
    axios.delete(`/api/v1/admin/coupons/${id}`)
        .then(res => {
            // remove coupon in list
            const index = coupons.findIndex(coupon => coupon.couponId === id);
            coupons.splice(index, 1);

            // render pagination
            renderPagination();

            toastr.success("Xóa coupon thành công");
        })
        .catch(err => {
            console.log(err);
            toastr.error(err.response.data.message);
        })
}

renderPagination();