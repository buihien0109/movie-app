// Render Supplier
const tableContent = document.querySelector("table tbody")
const renderSuppliers = (supplierList) => {
    tableContent.innerHTML = "";
    let html = "";
    supplierList.forEach(supplier => {
        html += `
                    <tr>
                        <td>
                            <img src="${supplier.thumbnail}" alt="" width="50px" height="50px" class="border rounded-circle" style="object-fit: cover">
                        </td>
                        <td>
                            <a href="/admin/suppliers/${supplier.supplierId}/detail">${supplier.name}</a>
                        </td>
                        <td>${supplier.phone}</td>
                        <td>${supplier.email}</td>
                        <td>${supplier.address}</td>
                    </tr>
                `
    })
    tableContent.innerHTML = html;
}

// render pagination using pagination.js
const renderPagination = () => {
    $('#pagination').pagination({
        dataSource: suppliers,
        className: 'paginationjs-theme-blue paginationjs-big',
        callback: function (data, pagination) {
            renderSuppliers(data);
        },
        hideOnlyOnePage: true
    })
}

renderPagination();