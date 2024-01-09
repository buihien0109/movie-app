// Render transactions
const tableContent = document.querySelector("table tbody")
const renderTransactions = (transactionList) => {
    tableContent.innerHTML = "";
    let html = "";
    transactionList.forEach(transaction => {
        html += `
                    <tr>
                        <td>
                            <a href="/admin/transactions/${transaction.id}/detail">${transaction.id}</a>
                        </td>
                        <td>
                            <a href="/admin/suppliers/${transaction.supplier.supplierId}/detail">${transaction.supplier.name}</a>
                        </td>
                        <td>${formatDate(transaction.transactionDate)}</td>
                        <td>${transaction.receiverName}</td>
                        <td>${transaction.senderName}</td>
                        <td>${formatCurrency(transaction.totalAmount)}</td>
                    </tr>
                `
    })
    tableContent.innerHTML = html;
}

// render pagination using pagination.js
const renderPagination = () => {
    $('#pagination').pagination({
        dataSource: transactions,
        className: 'paginationjs-theme-blue paginationjs-big',
        callback: function (data, pagination) {
            renderTransactions(data);
        },
        hideOnlyOnePage: true
    })
}

renderPagination();