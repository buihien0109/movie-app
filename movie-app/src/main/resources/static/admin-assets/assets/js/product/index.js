// render products from products to body of table
const tableBody = document.querySelector('table tbody');
const renderProducts = (productList) => {
    let html = '';
    productList.forEach(product => {
        html += `
                    <tr>
                        <td>
                            <a href="/admin/products/${product.productId}/detail">${product.name}</a>
                        </td>
                        <td>
                            ${formatCurrency(product.price)}
                        </td>
                        <td>${product.stockQuantity}</td>
                        <td>${productStatus(product.status)}</td>
                        <td>${product.categoryName}</td>
                         <td>
                            ${
                                product.supplierId
                                    ? `<a href="/admin/suppliers/${product.supplierId}/detail">${product.supplierName}</a>`
                                    : 'Unknown'
                            }
                        </td>
                    </tr>
                `;
    })
    tableBody.innerHTML = html;
}

// render pagination using pagination.js
const renderPagination = (productList) => {
    $('#pagination').pagination({
        dataSource: productList,
        className: 'paginationjs-theme-blue paginationjs-big',
        callback: function (data, pagination) {
            renderProducts(data);
        },
        hideOnlyOnePage: true
    })
}

const productStatus = status => {
    switch (status) {
        case 'AVAILABLE':
            return '<span class="badge badge-success">Còn hàng</span></>';
        case 'UNAVAILABLE':
            return '<span class="badge badge-danger">Hết hàng</span>';
        case 'CEASE':
            return '<span class="badge badge-warning">Ngừng kinh doanh</span>';
        case 'NOT_YET_SOLD':
            return '<span class="badge badge-secondary">Chưa mở bán</span>';
        default:
            return 'Không xác định';
    }
}

renderPagination(products);