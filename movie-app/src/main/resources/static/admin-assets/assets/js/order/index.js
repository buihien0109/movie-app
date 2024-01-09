// render orders from orders to body of table
const tableBody = document.querySelector('table tbody');
const renderOrders = (orderList) => {
    let html = '';
    orderList.forEach(order => {
        html += `
                    <tr>
                        <td>
                            <a href="/admin/orders/${order.orderNumber}/detail">${order.orderNumber}</a>
                        </td>
                        <td>
                            ${order.username}
                        </td>
                        <td>${order.email}</td>
                        <td>${order.phone}</td>
                        <td>${formatDate(order.orderDate)}</td>
                        <td>${orderStatus(order.status)}</td>
                    </tr>
                `;
    })
    tableBody.innerHTML = html;
}

// render pagination using pagination.js
const renderPagination = (orderList) => {
    $('#pagination').pagination({
        dataSource: orderList,
        className: 'paginationjs-theme-blue paginationjs-big',
        callback: function (data, pagination) {
            renderOrders(data);
        },
        hideOnlyOnePage: true
    })
}

const orderStatus = status => {
    switch (status) {
        case 'WAIT':
            return '<span class="badge badge-warning">Chờ xác nhận</span></>';
        case 'WAIT_DELIVERY':
            return '<span class="badge badge-dark">Chờ giao hàng</span></>';
        case 'DELIVERY':
            return '<span class="badge badge-primary">Đang giao</span>';
        case 'COMPLETE':
            return '<span class="badge badge-success">Đã giao</span>';
        case 'CANCELED':
            return '<span class="badge badge-secondary">Đã hủy</span>';
        case 'RETURNED':
            return '<span class="badge badge-danger">Đã trả hàng</span>';
        default:
            return 'Không xác định';
    }
}
renderPagination(orders);