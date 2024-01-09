let idUpdate = null;
let products = transaction.transactionItems.map(item => {
    return {
        productId: item.product.productId,
        productName: item.product.name,
        quantity: item.quantity,
        purchasePrice: item.purchasePrice
    }
});
console.log({products});

// format currency -> number no comma
const formatPrice = (price) => {
    return parseInt(price.replace(/,/g, ""));
}

//Initialize Select2 Elements
$('#product').select2({
    theme: 'bootstrap4',
    placeholder: 'Chọn sản phẩm'
})

$('#supplier').select2({
    theme: 'bootstrap4',
    placeholder: 'Chọn nhà cung cấp'
})

//Date picker
$('#date').daterangepicker({
    singleDatePicker: true,
    locale: {
        format: 'DD/MM/yyyy'
    }
})

// validate form
$('#form-update-transaction').validate({
    rules: {
        senderName: {
            required: true
        },
        receiverName: {
            required: true
        },
        supplier: {
            required: true
        }
    },
    messages: {
        senderName: {
            required: "Tên người gửi không được để trống"
        },
        receiverName: {
            required: "Tên người nhận không được để trống",
        },
        supplier: {
            required: "Nhà cung cấp không được để trống"
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

// Validate form add product
$.validator.addMethod(
    "validateFormat",
    function (value, element) {
        // check if value is number
        return Number.isInteger(Number(formatPrice(value)))
    },
    "Không đúng định dạng"
);
$.validator.addMethod(
    "validateMinValue",
    function (value, element) {
        // get value of type
        return formatPrice(value) > 0
    },
    "Giá trị giảm phải lớn hơn 0"
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
        },
        purchasePrice: {
            required: true,
            validateMinValue: true,
            validateFormat: true
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
        },
        purchasePrice: {
            required: "Giá nhập không được để trống",
            validateFormat: "Giá nhập không đúng định dạng",
            validateMinValue: "Giá nhập phải lớn hơn 0"
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

// format price when user input
const purchasePriceEl = document.getElementById('purchase-price');
purchasePriceEl.addEventListener('keyup', function (event) {
    const value = event.target.value;
    event.target.value = value.replace(/\D/g, "").replace(/\B(?=(\d{3})+(?!\d))/g, ",");
});

function openModalCreateProduct() {
    $('#modal-product').modal('show');

    // change title
    const modalTitle = document.querySelector('#modal-product .modal-title');
    modalTitle.innerHTML = 'Thêm sản phẩm';
}

function openModalUpdateProduct(id) {
    $('#modal-product').modal('show');

    // change title
    const modalTitle = document.querySelector('#modal-product .modal-title');
    modalTitle.innerHTML = 'Cập nhật sản phẩm';

    // fill data
    const product = products.find((product, index) => index === id);
    const quantity = document.getElementById('quantity');
    const purchasePrice = document.getElementById('purchase-price');

    $('#product').val(product.productId);
    $('#product').trigger('change');
    quantity.value = product.quantity;
    purchasePrice.value = formatCurrency(product.purchasePrice);

    // disable product select
    $('#product').prop('disabled', true);

    idUpdate = id;
}

// add event modal hidden
$('#modal-product').on('hidden.bs.modal', function (e) {
    // clear input
    const productSelected = document.getElementById('product');
    const quantity = document.getElementById('quantity');
    const purchasePrice = document.getElementById('purchase-price');

    $('#product').val("");
    $('#product').trigger('change');
    quantity.value = '';
    purchasePrice.value = '';

    // enable product select
    $('#product').prop('disabled', false);

    idUpdate = null;
})

const renderProducts = () => {
    const productSelected = document.getElementById('product-selected');
    productSelected.innerHTML = '';
    products.forEach((product, index) => {
        const productSelectedHtml = `
                    <tr>
                        <td>${index + 1}</td>
                        <td>${product.productName}</td>
                        <td>${product.quantity}</td>
                        <td>${formatCurrency(product.purchasePrice)}</td>
                        <td>${formatCurrency(product.quantity * product.purchasePrice)}</td>
                        <td>
                             <button type="button" class="btn btn-primary btn-sm" onclick="openModalUpdateProduct(${index})">
                                <i class="fas fa-pen"></i>
                            </button>
                            <button type="button" class="btn btn-danger btn-sm" onclick="deleteProduct(${product.productId})">
                                <i class="fas fa-trash"></i>
                            </button>
                        </td>
                    </tr>
                `;
        productSelected.innerHTML += productSelectedHtml;
    })
    // render total amount at the end of table
    const totalAmount = getTotalAmount();
    const totalAmountHtml = `
                    <tr>
                        <td colspan="4" class="text-right">Tổng tiền</td>
                        <td colspan="1">${formatCurrency(totalAmount)}</td>
                    </tr>
                `;
    productSelected.innerHTML += totalAmountHtml;

    // update total product
    const totalProduct = document.getElementById('total-product');
    totalProduct.innerHTML = products.length;
}

const getTotalAmount = () => {
    return products.reduce((total, product) => {
        return total + product.quantity * product.purchasePrice;
    }, 0);
}

const deleteProduct = (productId) => {
    const isDelete = confirm('Bạn có chắc chắn muốn xóa sản phẩm này?');
    if (!isDelete) return;

    // find transaction item by productId in transaction object
    const transactionItem = transaction.transactionItems.find(item => item.product.productId === productId);
    console.log(transactionItem)

    // call api delete product using axios
    axios.delete(`/api/v1/admin/transaction-items/${transactionItem.id}`)
        .then(response => {
            toastr.success('Xóa sản phẩm thành công');
            // remove product in products array
            products = products.filter(product => product.productId !== productId);

            // remove transaction item in transaction object
            transaction.transactionItems = transaction.transactionItems.filter(item => item.product.productId !== productId);
            renderProducts();
        })
        .catch(error => {
            console.log(error)
            toastr.error(error.response.data.message);
        })
}

const product = document.getElementById('product');
const quantity = document.getElementById('quantity');
const purchasePrice = document.getElementById('purchase-price');

// handle add product
const addProduct = () => {
    if (!$('#form-product').valid()) return;

    const quantityValue = quantity.value;
    const purchasePriceValue = Number(formatPrice(purchasePrice.value));
    const productSelectedId = product.value;
    const productSelectedName = product.options[product.selectedIndex].text;

    // check product exist
    const productExist = products.find(product => product.productId === productSelectedId);
    if (productExist) {
        toastr.error('Sản phẩm đã tồn tại');
        return;
    }

    // call api create transaction item using axios
    const data = {
        transactionId: transaction.id,
        productId: productSelectedId,
        quantity: quantityValue,
        purchasePrice: purchasePriceValue
    }

    axios.post('/api/v1/admin/transaction-items', data)
        .then(response => {
            toastr.success('Thêm sản phẩm thành công');

            // add product to products array
            const productSelectedObject = {
                productId: productSelectedId,
                productName: productSelectedName,
                quantity: quantityValue,
                purchasePrice: purchasePriceValue
            }
            products.push(productSelectedObject);
            renderProducts();

            // clear input
            quantity.value = '';
            purchasePrice.value = '';

            // close modal
            $('#modal-product').modal('hide');
        })
        .catch(error => {
            console.log(error)
            toastr.error(error.response.data.message);
        })
}

// handle update product
const updateProduct = () => {
    if (!$('#form-product').valid()) return;

    // find product by idUpdate
    const quantityValue = quantity.value;
    const purchasePriceValue = Number(formatPrice(purchasePrice.value));
    const productSelectedId = product.value;
    const productSelectedName = product.options[product.selectedIndex].text;

    // check product exist and different idUpdate
    const productExist = products.find((product, index) => product.productId === productSelectedId && index !== idUpdate);
    if (productExist) {
        toastr.error('Sản phẩm đã tồn tại');
        return;
    }

    // call api update transaction item using axios
    const transactionItem = transaction.transactionItems.find((item, index) => index === idUpdate);
    const data = {
        transactionId: transaction.id,
        productId: productSelectedId,
        quantity: quantityValue,
        purchasePrice: purchasePriceValue
    }

    axios.put(`/api/v1/admin/transaction-items/${transactionItem.id}`, data)
        .then(response => {
            const productSelectedObject = {
                productId: productSelectedId,
                productName: productSelectedName,
                quantity: quantityValue,
                purchasePrice: purchasePriceValue
            }

            // find product by idUpdate and update
            for (let i = 0; i < products.length; i++) {
                if (i === idUpdate) {
                    products[i] = productSelectedObject;
                    break;
                }
            }

            renderProducts();

            // clear input
            quantity.value = '';
            purchasePrice.value = '';

            // reset idUpdate
            idUpdate = null;

            // close modal
            $('#modal-product').modal('hide');

            toastr.success('Cập nhật sản phẩm thành công');
        })
        .catch(error => {
            console.log(error)
            toastr.error(error.response.data.message);
        })
}

const btnHandle = document.getElementById('btn-handle');
btnHandle.addEventListener("click", () => {
    if (idUpdate === null) {
        addProduct();
    } else {
        updateProduct();
    }
})

// add event handle for create button and send data to server using axios
const btnUpdate = document.getElementById('btn-update');
const senderName = document.getElementById('sender-name');
const receiverName = document.getElementById('receiver-name');
const supplier = document.getElementById('supplier');
const date = document.getElementById('date');

btnUpdate.addEventListener("click", () => {
    if (!$('#form-update-transaction').valid()) return;

    const senderNameValue = senderName.value;
    const receiverNameValue = receiverName.value;
    const supplierValue = supplier.value;
    const dateValue = date.value;

    const data = {
        senderName: senderNameValue,
        receiverName: receiverNameValue,
        supplierId: Number(supplierValue),
        transactionDate: new Date(dateValue)
    }
    console.log(data)

    axios.put(`/api/v1/admin/transactions/${transaction.id}`, data)
        .then(response => {
            if (response.status === 200) {
                toastr.success('Cập nhật phiếu nhập hàng thành công');
            } else {
                toastr.error('Cập nhật phiếu nhập hàng thất bại');
            }
        })
        .catch(error => {
            console.log(error)
            toastr.error(error.response.data.message);
        })
})

renderProducts();