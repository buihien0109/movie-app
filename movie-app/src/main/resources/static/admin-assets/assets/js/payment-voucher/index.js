// Render Supplier
const tableContent = document.querySelector("table tbody")
const renderPaymentVouchers = (paymentVoucherList) => {
    tableContent.innerHTML = "";
    let html = "";
    paymentVoucherList.forEach(paymentVoucher => {
        html += `
            <tr>
                <td>
                    <a href="/admin/payment_vouchers/${paymentVoucher.id}/detail">${paymentVoucher.id}</a></td>
                <td>
                    ${formatDate(paymentVoucher.createdAt)}
                </td>
                <td>${paymentVoucher.purpose}</td>
                <td>${formatCurrency(paymentVoucher.amount)}</td>
                <td>
                    <a href="/admin/users/${paymentVoucher.user.userId}/detail">${paymentVoucher.user.username}</a></td>
                </td>
                 <td>${paymentVoucher.note}</td>
            </tr>
        `
    })
    tableContent.innerHTML = html;
}

// render pagination using pagination.js
const renderPagination = () => {
    $('#pagination').pagination({
        dataSource: paymentVouchers,
        className: 'paginationjs-theme-blue paginationjs-big',
        callback: function (data, pagination) {
            renderPaymentVouchers(data);
        },
        hideOnlyOnePage: true
    })
}

renderPagination();