// Xử lý khi bấm vào nút mua phim
const btnModalPurchase = document.getElementById('btn-modal-purchase');
const modalPurchase = new bootstrap.Modal(document.getElementById('modal-purchase'));
btnModalPurchase.addEventListener('click', () => {
    // Nếu người dùng chưa đăng nhập thì thông báo
    if (!currentUser) {
        toastr.warning('Vui lòng đăng nhập để mua phim');
        return;
    }

    // Hiển thị modal mua phim
    modalPurchase.show();
})

// Xử lý khi ấn vào các hình thức thanh toán
const paymentItems = document.querySelectorAll('.payment-item input[type="radio"]');
paymentItems.forEach(item => {
    item.addEventListener('change', () => {
        if (item.value === 'BANK_TRANSFER') {
            displayBankTransfer();
        } else if (item.value === 'MOMO') {
            hideBankTransfer();
            toastr.warning('Thanh toán bằng Ví MoMo đang trong quá trình phát triển. Vui lòng chọn hình thức thanh toán khác.');
        } else if (item.value === 'ZALO_PAY') {
            hideBankTransfer();
            toastr.warning('Thanh toán bằng Ví ZaloPay đang trong quá trình phát triển. Vui lòng chọn hình thức thanh toán khác.');
        }
    })
})

const displayBankTransfer = () => {
    const infoBankTransfer = document.querySelector('.info-bank-transfer');
    infoBankTransfer.classList.remove('d-none');
}

const hideBankTransfer = () => {
    const infoBankTransfer = document.querySelector('.info-bank-transfer');
    infoBankTransfer.classList.add('d-none');
}

// Xử lý khi ấn vào nút xác nhận mua hàng
const btnSubmitOrder = document.getElementById('btn-submit-order');
btnSubmitOrder.addEventListener('click', () => {
    const paymentMethod = document.querySelector('input[name="payment-method"]:checked').value;
    if (paymentMethod !== 'BANK_TRANSFER') {
        toastr.warning('Hình thức thanh toán đang trong quá trình phát triển. Vui lòng chọn hình thức thanh toán khác.');
    }

    const data = {
        filmId: film.id,
        paymentMethod: paymentMethod
    }

    // Gọi api server để tạo đơn hàng sử dụng axios
    axios.post('/api/orders', data)
        .then(response => {
            // Thay đổi nút mua phim thành "Đang chờ xác nhận"
            btnSubmitOrder.innerHTML = 'Đang chờ xác nhận';
            btnSubmitOrder.disabled = true;

            // Nếu tạo đơn hàng thành công thì ẩn modal mua phim
            toastr.success('Đã tạo đơn hàng thành công. Vui lòng kiểm tra email để xem thông tin chi tiết');

            setTimeout(() => {
                modalPurchase.hide();
            }, 1500);
        }).catch(error => {
        console.log(error);
        toastr.error(error.response.data.message);
    })
})