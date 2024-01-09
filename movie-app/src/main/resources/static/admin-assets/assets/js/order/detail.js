const formatPrice = (price) => {
    return parseInt(price.replace(/,/g, ""));
}

// ----------------- handle add product -----------------
// init select2
$('#product').select2({
    theme: 'bootstrap4',
    placeholder: 'Chọn sản phẩm'
})

$('#user').select2({
    theme: 'bootstrap4',
    placeholder: 'Chọn user'
})

// validate form
$.validator.addMethod(
    "validateFormat",
    function (value, element) {
        // check if value is number
        return Number.isInteger(Number(formatPrice(value)))
    },
    "Không đúng định dạng"
);

$('#form-product').validate({
    rules: {
        product: {
            required: true
        },
        quantity: {
            required: true,
            validateFormat: true,
            min: 1,
        }
    },
    messages: {
        product: {
            required: "Tên sản phẩm không được để trống"
        },
        quantity: {
            required: "Số lượng không được để trống",
            min: "Số lượng phải lớn hơn 0",
            validateFormat: "Số lượng không đúng định dạng"
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

let productsSelected = [];
let idUpdate = null;
let userSelected = null;
let userAddressSelected = null;
let userAddressList = [];
let couponSelected = null;
const modalTitleEl = document.querySelector('#modal-product .modal-title');
const quantityEl = document.getElementById('quantity');
const productEl = document.getElementById('product');

function openModalCreateProduct() {
    $('#modal-product').modal('show');

    // change title
    modalTitleEl.innerHTML = 'Thêm sản phẩm';
}

function openModalUpdateProduct(productId) {
    $('#modal-product').modal('show');

    // change title
    modalTitleEl.innerHTML = 'Cập nhật sản phẩm';

    // fill data
    const product = productsSelected.find(product => product.productId === productId);

    // Trigger value product using select2
    $('#product').val(product.productId);
    $('#product').trigger('change');
    quantityEl.value = product.quantity;

    idUpdate = productId;
    productEl.disabled = true;
}

// add event modal hidden
$('#modal-product').on('hidden.bs.modal', function (e) {
    $('#product').val("");
    $('#product').trigger('change');
    quantityEl.value = '';

    idUpdate = null;
    productEl.disabled = false;
})

const productTableContentEl = document.getElementById('product-table-content');
const renderProducts = () => {
    productTableContentEl.innerHTML = '';
    productsSelected.forEach((product, index) => {
        const html = `
                <tr>
                    <td>${index + 1}</td>
                    <td>${product.productName}</td>
                    <td>${product.quantity}</td>
                    <td>${formatCurrency(product.price)}</td>
                    <td>${product.discountPrice ? formatCurrency(product.discountPrice) : ''}</td>
                    <td>${product.discountPrice ? formatCurrency(product.quantity * product.discountPrice) : formatCurrency(product.quantity * product.price)}</td>
                    <td class="btn-action">
                         <button type="button" class="btn btn-primary btn-sm" onclick="openModalUpdateProduct(${product.productId})">
                            <i class="fas fa-pen"></i>
                        </button>
                        <button type="button" class="btn btn-danger btn-sm" onclick="deleteProduct(${product.productId})">
                            <i class="fas fa-trash"></i>
                        </button>
                    </td>
                </tr>
            `;
        productTableContentEl.innerHTML += html;
    })

    renderAmount();

    // update total product
    const totalProduct = document.getElementById('total-product');
    totalProduct.innerHTML = productsSelected.length.toString();

    // if productsSelected is empty remove button coupon and modal coupon. otherwise, add button coupon and modal coupon
    if (productsSelected.length === 0) {
        hideCoupon();
    } else {
        showCoupon();
    }
}

const renderAmount = () => {
    const amountEls = document.querySelectorAll(".amount");
    if (amountEls.length > 0) {
        amountEls.forEach(amountEl => {
            amountEl.parentElement.removeChild(amountEl);
        })
    }

    // render total amount at the end of table
    const subtotalAmount = getSubtotalAmount();
    const discountAmount = getDiscountAmount();
    const totalAmount = getTotalAmount();
    console.log({couponSelected, subtotalAmount, discountAmount, totalAmount})

    const amountHtml = `
        <tr class="amount">
            <td colspan="5" class="text-right">Thành tiền</td>
            <td colspan="1">${formatCurrency(subtotalAmount)}</td>
        </tr>
        <tr class="amount">
            <td colspan="5" class="text-right">Giảm giá ${couponSelected ? `(${couponSelected.discount}%)` : ''}</td>
            <td colspan="1">${formatCurrency(discountAmount)}</td>
        </tr>
        <tr class="amount">
            <td colspan="5" class="text-right">Tổng tiền</td>
            <td colspan="1">${formatCurrency(totalAmount)}</td>
        </tr>
    `;

    productTableContentEl.innerHTML += amountHtml;
}

const getSubtotalAmount = () => {
    return productsSelected.reduce((total, product) => {
        if (product.discountPrice != null) {
            return total + product.quantity * product.discountPrice;
        }
        return total + product.quantity * product.price;
    }, 0);
}

const getDiscountAmount = () => {
    if (couponSelected) {
        return Math.round(getSubtotalAmount() * couponSelected.discount / 100);
    }
    return 0;
}

const getTotalAmount = () => {
    return getSubtotalAmount() - getDiscountAmount();
}

const deleteProduct = (productId) => {
    const isDelete = confirm('Bạn có chắc chắn muốn xóa sản phẩm này?');
    if (!isDelete) return;

    // get order item by productId
    const orderItem = order.orderItems.find(orderItem => orderItem.product.productId === productId);
    console.log({orderItem})

    // call api delete order item
    axios.delete(`/api/v1/admin/order-items/${orderItem.orderItemId}`)
        .then(response => {
            if (response.status === 200) {
                toastr.success('Xóa sản phẩm thành công');
                // remove order item in order
                order.orderItems = order.orderItems.filter(orderItem => orderItem.product.productId !== productId);

                // remove product in productsSelected
                productsSelected = productsSelected.filter(product => product.productId !== productId);
                renderProducts();
            } else {
                toastr.error('Xóa sản phẩm thất bại');
            }
        })
        .catch(error => {
            console.log(error);
            toastr.error(error.response.data.message);
        })
}

// handle add product
const addProduct = () => {
    const quantityValue = Number(quantityEl.value);
    const productSelectedId = Number(productEl.value);
    const productSelectedName = productEl.options[productEl.selectedIndex].text;

    // check product exist
    const productExist = productsSelected.find(product => product.productId === productSelectedId);
    if (productExist) {
        toastr.error('Sản phẩm đã tồn tại');
        return;
    }
    const productInfo = productList.find(product => product.productId === productSelectedId);

    // check quantity
    if (quantityValue > productInfo.stockQuantity) {
        toastr.error('Số lượng sản phẩm không đủ');
        return;
    }

    // call api create order item
    const data = {
        orderId: order.orderId,
        productId: productSelectedId,
        quantity: quantityValue,
        price: productInfo.discountPrice ? productInfo.discountPrice : productInfo.price,
    }

    axios.post('/api/v1/admin/order-items', data)
        .then(response => {
            if (response.status === 200) {
                toastr.success('Thêm sản phẩm thành công');
                // add order item to order
                order.orderItems.push(response.data);

                // add product to productsSelected
                const productSelectedObject = {
                    productId: productSelectedId,
                    productName: productSelectedName,
                    quantity: quantityValue,
                    price: productInfo.price,
                    discountPrice: productInfo.discountPrice
                }
                productsSelected.push(productSelectedObject);
                renderProducts();

                // clear input
                quantityEl.value = '';

                // close modal
                $('#modal-product').modal('hide');
            } else {
                toastr.error('Thêm sản phẩm thất bại');
            }
        })
        .catch(error => {
            console.log(error);
            toastr.error(error.response.data.message);
        })
}

// handle update product
const updateProduct = () => {
    const quantityValue = Number(quantityEl.value)
    const productSelectedId = Number(productEl.value);
    const productSelectedName = productEl.options[productEl.selectedIndex].text;

    // check product exist and different idUpdate
    const productExist = productsSelected.find((product) => product.productId === productSelectedId && product.productId !== idUpdate);
    if (productExist) {
        toastr.error('Sản phẩm đã tồn tại');
        return;
    }

    const productInfo = productList.find(product => product.productId === productSelectedId);

    // calculate difference between old quantity and new quantity and quantity of product in stock
    const productSelected = productsSelected.find((product => product.productId === idUpdate));
    const differenceQuantity = quantityValue - productSelected.quantity;
    if (differenceQuantity > productInfo.stockQuantity) {
        toastr.error('Số lượng sản phẩm không đủ');
        return;
    }

    // call api update order item
    const data = {
        orderId: order.orderId,
        productId: productSelectedId,
        quantity: quantityValue,
        price: productInfo.discountPrice ? productInfo.discountPrice : productInfo.price,
    }

    // get order item by productId
    const orderItem = order.orderItems.find(orderItem => orderItem.product.productId === idUpdate);
    console.log({orderItem})

    // call api update order item
    axios.put(`/api/v1/admin/order-items/${orderItem.orderItemId}`, data)
        .then(response => {
            if (response.status === 200) {
                toastr.success('Cập nhật sản phẩm thành công');
                // update order item in order
                orderItem.quantity = quantityValue;

                // update product in productsSelected
                const productSelectedObject = {
                    productId: productSelectedId,
                    productName: productSelectedName,
                    quantity: quantityValue,
                    price: productInfo.price,
                    discountPrice: productInfo.discountPrice
                }

                // find product by idUpdate and update
                for (let i = 0; i < productsSelected.length; i++) {
                    if (i === idUpdate) {
                        productsSelected[i] = productSelectedObject;
                        break;
                    }
                }

                renderProducts();

                // clear input
                quantityEl.value = '';

                // reset idUpdate
                idUpdate = null;

                // close modal
                $('#modal-product').modal('hide');
            } else {
                toastr.error('Cập nhật sản phẩm thất bại');
            }
        })
        .catch(error => {
            console.log(error);
            toastr.error(error.response.data.message);
        })
}

const btnHandle = document.getElementById('btn-handle');
btnHandle.addEventListener("click", () => {
    if (!$("#form-product").valid()) return;

    if (idUpdate === null) {
        addProduct();
    } else {
        updateProduct();
    }
})

// ----------------- handle province -----------------
// Hiển thị danh sách province vào select id="province"
const provinceSelect = document.getElementById('province');
const districtSelect = document.getElementById('district');
const wardSelect = document.getElementById('ward');

const renderProvince = (provinces) => {
    provinceSelect.innerHTML = '<option hidden="hidden" value="">-- Chọn Tỉnh/Thành phố</option>';
    districtSelect.innerHTML = '<option hidden="hidden" value="">-- Chọn Quận/Huyện</option>';
    wardSelect.innerHTML = '<option hidden="hidden" value="">-- Chọn Xã/Phường</option>';

    provinces.forEach(province => {
        const {ProvinceID, ProvinceName} = province;
        const option = document.createElement('option');
        option.value = ProvinceID;
        option.innerText = ProvinceName;
        option.setAttribute('data-province-name', ProvinceName);
        provinceSelect.appendChild(option);
    });
}

const renderDistrict = (districts) => {
    districtSelect.innerHTML = '<option hidden="hidden" value="">-- Chọn Quận/Huyện</option>';
    districts.forEach(district => {
        const {DistrictID, DistrictName} = district;
        const option = document.createElement('option');
        option.value = DistrictID;
        option.innerText = DistrictName;
        option.setAttribute('data-district-name', DistrictName);
        districtSelect.appendChild(option);
    });
}

const renderWard = (wards) => {
    wardSelect.innerHTML = '<option hidden="hidden" value="">-- Chọn Xã/Phường</option>';
    wards.forEach(ward => {
        const {WardCode, WardName} = ward;
        const option = document.createElement('option');
        option.value = WardCode;
        option.innerText = WardName;
        option.setAttribute('data-ward-name', WardName);
        wardSelect.appendChild(option);
    });
}

const getProvinces = async () => {
    try {
        const response = await axios.get('/api/v1/public/address/provinces');
        if (response.status === 200) {
            const {data} = response.data;
            renderProvince(data);
            districtSelect.disabled = true;
            wardSelect.disabled = true;
        }
    } catch (error) {
        console.log(error);
        toastr.error('Không thể lấy danh sách tỉnh/thành phố');
    }
}

provinceSelect.addEventListener('change', (event) => {
    const provinceCode = event.target.value;
    getDistricts(provinceCode);
    districtSelect.disabled = false;
    wardSelect.disabled = true;
    wardSelect.innerHTML = '<option hidden="hidden">-- Chọn Xã/Phường</option>';
});

const getDistricts = async (provinceId) => {
    try {
        const response = await axios.get(`/api/v1/public/address/districts?province_id=${provinceId}`);
        if (response.status === 200) {
            const {data} = response.data;
            renderDistrict(data);
        }
    } catch (error) {
        console.log(error);
        toastr.error('Không thể lấy danh sách quận/huyện');
    }
}

districtSelect.addEventListener('change', (event) => {
    const districtId = event.target.value;
    getWards(districtId);
    wardSelect.disabled = false;
});

const getWards = async (districtId) => {
    try {
        const response = await axios.get(`/api/v1/public/address/wards?district_id=${districtId}`);
        if (response.status === 200) {
            const {data} = response.data;
            renderWard(data);
        }
    } catch (error) {
        console.log(error);
        toastr.error('Không thể lấy danh sách xã/phường');
    }
}

// ----------------- handle add user -----------------
$('#form-user').validate({
    rules: {
        user: {
            required: true
        }
    },
    messages: {
        user: {
            required: "User không được để trống"
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

const userInformation = document.getElementById('user-info');
const userEl = document.getElementById('user');
const btnChooseUser = document.getElementById('btn-choose-user');
const nameEl = document.getElementById('name');
const phoneEl = document.getElementById('phone');
const emailEl = document.getElementById('email');
const addressEl = document.getElementById('address');

btnChooseUser.addEventListener('click', async () => {
    if (!$("#form-user").valid()) return;

    // confirm choose user
    const isChoose = confirm('Bạn có chắc chắn muốn chọn user này?');
    if (!isChoose) return;

    const userId = Number(userEl.value);
    const user = userList.find(user => user.userId === Number(userId));
    if (user) {
        await getAddressOfUser(userId);
        displayUserSelected(user);
        fillDataUserSelected(user);

        // update userSelected
        userSelected = user;

        // close modal
        $('#modal-user').modal('hide');
    }
})

const displayUserSelected = (userSelected) => {
    userInformation.innerHTML = `
        <div class="card">
            <div class="card-body">
                <h5 class="mb-4 text-muted">User đang chọn</h5>
                <div class="d-flex">
                    <div class="d-flex flex-column justify-content-center align-items-center">
                        <img src="${userSelected.avatar}" alt="" class="rounded-circle" width="100" height="100">
                        <span class="d-inline-block badge badge-secondary mt-3">${userSelected.username}</span>
                    </div>
                    <div class="ml-4 d-flex flex-column justify-content-center align-items-center">
                        <a href="javascript:void(0)" type="button" data-toggle="modal" data-target="#modal-address">Các địa chỉ nhận hàng của user</a>
                        <button class="btn btn-danger mt-3" onclick="cancelUser()">Hủy chọn user</button>
                    </div>
                </div>
            </div>
        </div>
        <div class="modal fade" id="modal-address">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h4 class="modal-title">Chọn địa chỉ</h4>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">×</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <table class="table table-bordered table-hover">
                            <thead>
                                <tr>
                                    <th>STT</th>
                                    <th>Địa chỉ</th>
                                    <th>Mặc định</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                ${userAddressList.map((address, index) => {
        return `
                                        <tr>
                                            <td>${index + 1}</td>
                                            <td>${address.detail}, ${address.ward}, ${address.district}, ${address.province}</td>
                                            <td>${address.isDefault ? '<span class="text-success"><i class="fas fa-check"></i></span>' : ''}</td>
                                            <td>
                                                <button class="btn btn-primary btn-sm" onclick="chooseAddress(${index})">
                                                    Chọn
                                                </button>
                                            </td>
                                        </tr>
                                    `
    }).join('')}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    `
}

const chooseAddress = async (index) => {
    const address = userAddressList[index];
    await fillAddress(address);
    userAddressSelected = address;
    $('#modal-address').modal('hide');
}

const fillDataUserSelected = (userSelected) => {
    nameEl.value = userSelected.username;
    phoneEl.value = userSelected.phone;
    emailEl.value = userSelected.email;
}

const fillAddressUserSelected = async (addresses) => {
    clearAddress();

    if (addresses.length === 0) return;

    // find address default
    const addressDefault = addresses.find(address => address.isDefault === true);
    if (addressDefault) {
        await fillAddress(addressDefault);
        userAddressSelected = addressDefault;
    }
}

const fillAddress = async (address) => {
    addressEl.value = address.detail;

    // choose province by province name to select element. value of select element is provinceId and content is provinceName
    provinceSelect.querySelector(`option[data-province-name="${address.province}"]`).selected = true;

    // get districts of province
    await getDistricts(provinceSelect.value);

    // choose district by district name to select element. value of select element is districtId and content is districtName
    districtSelect.querySelector(`option[data-district-name="${address.district}"]`).selected = true;

    // get wards of district
    await getWards(districtSelect.value);

    // choose ward by ward name to select element. value of select element is wardId and content is wardName
    wardSelect.querySelector(`option[data-ward-name="${address.ward}"]`).selected = true;

    // enable district and ward
    districtSelect.disabled = false;
    wardSelect.disabled = false;
}

const getAddressOfUser = async (userId) => {
    try {
        const res = await axios.get(`/api/v1/admin/users/${userId}/addresses`);
        if (res.status === 200) {
            await fillAddressUserSelected(res.data);
            userAddressList = res.data;
        } else {
            toastr.error('Không thể lấy địa chỉ của user');
        }
    } catch (error) {
        console.log(error);
        toastr.error(error.response.data.message);
    }
}

const getAddressOfUserNotFillData = async (userId) => {
    try {
        const res = await axios.get(`/api/v1/admin/users/${userId}/addresses`);
        if (res.status === 200) {
            userAddressList = res.data;
        } else {
            toastr.error('Không thể lấy địa chỉ của user');
        }
    } catch (error) {
        console.log(error);
        toastr.error(error.response.data.message);
    }
}

const clearAddress = () => {
    addressEl.value = '';
    provinceSelect.value = '';
    districtSelect.value = '';
    wardSelect.value = '';

    districtSelect.disabled = true;
    wardSelect.disabled = true;
}

const cancelUser = () => {
    // confirm cancel user
    const isCancel = confirm('Bạn có chắc chắn muốn hủy chọn user?');
    if (!isCancel) return;

    // clear input
    nameEl.value = '';
    phoneEl.value = '';
    emailEl.value = '';

    // clear address
    clearAddress();

    // reset userSelected
    userSelected = null;

    userInformation.innerHTML = '';
}

// ----------------- handle add coupon -----------------
const couponInfoEl = document.getElementById('coupon-info');
const hideCoupon = () => {
    couponInfoEl.innerHTML = '';
}

const showCoupon = () => {
    couponInfoEl.innerHTML = `
        <button class="btn btn-default btn-action" onclick="cancelCoupon()">Hủy mã giảm giá</button>
        <button class="btn btn-secondary btn-action" type="button" data-toggle="modal" data-target="#modal-coupon">Thêm mã giảm giá</button>
        <div class="modal fade" id="modal-coupon">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h4 class="modal-title">Chọn mã giảm giá</h4>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">×</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <table class="table table-bordered table-hover">
                            <thead>
                            <tr>
                                <th>Coupon code</th>
                                <th>Mức giảm</th>
                                <th>Tổng tiền</th>
                                <th>Tổng tiền sau khuyến mại</th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>
                                ${renderCoupon()}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    `
}

const renderCoupon = () => {
    let html = '';
    couponList.forEach((coupon, index) => {
        html += `
            <tr>
                <td>${coupon.code}</td>
                <td>${coupon.discount}</td>
                <td>${formatCurrency(getSubtotalAmount())}</td>
                <td>${formatCurrency(Math.round(getSubtotalAmount() * (100 - coupon.discount) / 100))}</td>
                <td>
                    <button class="btn btn-primary btn-sm" onclick="chooseCoupon(${index})">
                        Chọn
                    </button>
                </td>
            </tr>
        `
    })
    return html;
}

const chooseCoupon = (index) => {
    couponSelected = couponList[index];
    renderAmount();
    $('#modal-coupon').modal('hide');
}

const cancelCoupon = () => {
    couponSelected = null;
    renderAmount();
}

// ----------------- handle create order -----------------
$('#form-update-order').validate({
    rules: {
        name: {
            required: true
        },
        phone: {
            required: true,
            pattern: /^(0\d{9}|(\+84|84)[1-9]\d{8})$/
        },
        email: {
            required: true,
            email: true
        },
        province: {
            required: true
        },
        district: {
            required: true
        },
        ward: {
            required: true
        },
        address: {
            required: true
        },
        payment: {
            required: true
        },
        shipping: {
            required: true
        }
    },
    messages: {
        name: {
            required: "Họ tên không được để trống",
        },
        phone: {
            required: "Số điện thoại không được để trống",
            pattern: "Số điện thoại không đúng định dạng"
        },
        email: {
            required: "Email không được để trống",
            email: "Email không đúng định dạng"
        },
        province: {
            required: "Tỉnh/Thành phố không được để trống",
        },
        district: {
            required: "Quận/Huyện không được để trống",
        },
        ward: {
            required: "Xã/Phường không được để trống",
        },
        address: {
            required: "Địa chỉ không được để trống",
        },
        payment: {
            required: "Phương thức thanh toán không được để trống",
        },
        shipping: {
            required: "Phương thức vận chuyển không được để trống",
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

const btnUpdateOrder = document.getElementById('btn-update');
btnUpdateOrder.addEventListener('click', async () => {
    if (!$("#form-update-order").valid()) return;

    // check product selected
    if (productsSelected.length === 0) {
        toastr.error('Vui lòng chọn sản phẩm');
        return;
    }

    // create order request
    const username = document.getElementById('name').value;
    const phone = document.getElementById('phone').value;
    const email = document.getElementById('email').value;
    const address = document.getElementById('address').value;
    const note = document.getElementById('note').value;
    const shippingMethod = document.getElementById('shipping').value;
    const paymentMethod = document.getElementById('payment').value;
    const couponCode = couponSelected ? couponSelected.code : null;
    const couponDiscount = couponSelected ? couponSelected.discount : null;

    // get province selected and get province name by attribute data-province-name
    const provinceSelected = provinceSelect.options[provinceSelect.selectedIndex];
    const provinceName = provinceSelected.getAttribute('data-province-name');

    // get district selected and get district name by attribute data-district-name
    const districtSelected = districtSelect.options[districtSelect.selectedIndex];
    const districtName = districtSelected.getAttribute('data-district-name');

    // get ward selected and get ward name by attribute data-ward-name
    const wardSelected = wardSelect.options[wardSelect.selectedIndex];
    const wardName = wardSelected.getAttribute('data-ward-name');

    const data = {
        userId: userSelected ? userSelected.userId : null,
        username,
        phone,
        email,
        province: provinceName,
        district: districtName,
        ward: wardName,
        address,
        note,
        shippingMethod,
        paymentMethod,
        couponCode,
        couponDiscount
    }
    console.log({data})

    axios.put(`/api/v1/admin/orders/${order.orderId}`, data)
        .then(response => {
            if (response.status === 200) {
                console.log(response.data);
                toastr.success('Cập nhật đơn hàng thành công');
            } else {
                toastr.error('Cập nhật đơn hàng thất bại');
            }
        })
        .catch(error => {
            console.log(error);
            toastr.error(error.response.data.message);
        })
})

// ----------------- handle cancel order -----------------
const cancelOrder = () => {
    const isCancel = confirm('Bạn có chắc chắn muốn hủy đơn hàng này?');
    if (!isCancel) return;

    axios.put(`/api/v1/admin/orders/${order.orderId}/cancel`)
        .then(response => {
            if (response.status === 200) {
                toastr.success('Hủy đơn hàng thành công');
                removeBtnAction();
            } else {
                toastr.error('Hủy đơn hàng thất bại');
            }
        })
        .catch(error => {
            console.log(error);
            toastr.error(error.response.data.message);
        })
}

// ----------------- init data -----------------
const removeBtnAction = () => {
    const btns = document.querySelectorAll('.btn-action');
    btns.forEach(btn => {
        btn.parentElement.removeChild(btn);
    })
}
const initAddressUser = async () => {
// choose province by province name to select element. value of select element is provinceId and content is provinceName
    console.log(order)
    provinceSelect.querySelector(`option[data-province-name="${order.province}"]`).selected = true;

    // get districts of province
    await getDistricts(provinceSelect.value);

    // choose district by district name to select element. value of select element is districtId and content is districtName
    districtSelect.querySelector(`option[data-district-name="${order.district}"]`).selected = true;

    // get wards of district
    await getWards(districtSelect.value);

    // choose ward by ward name to select element. value of select element is wardId and content is wardName
    wardSelect.querySelector(`option[data-ward-name="${order.ward}"]`).selected = true;

    // enable district and ward
    districtSelect.disabled = false;
    wardSelect.disabled = false;
}
const init = async () => {
    // check order has userId
    if (order.user) {
        await getAddressOfUserNotFillData(order.user.userId);
        await getProvinces();
        await initAddressUser();
        userSelected = userList.find(user => user.userId === order.user.userId);
        displayUserSelected(userSelected);
    } else {
        await getProvinces();
        await initAddressUser();
    }

    // init productsSelected
    productsSelected = order.orderItems.map(orderItem => {
        return {
            productId: orderItem.product.productId,
            productName: orderItem.product.name,
            quantity: orderItem.quantity,
            price: orderItem.product.price,
            discountPrice: orderItem.product.price !== orderItem.price ? orderItem.price : null
        }
    })
    couponSelected = (order.couponDiscount && order.couponDiscount) ? {
        code: order.couponCode,
        discount: order.couponDiscount
    } : null;
    renderProducts();
    if (order.status !== 'WAIT' && order.status !== 'WAIT_DELIVERY') {
        removeBtnAction();
    }
}
init();
