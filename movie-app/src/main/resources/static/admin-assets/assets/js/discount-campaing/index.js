
// Generate status discount
const generateStatus = (status) => {
    switch (status) {
        case "ACTIVE":
            return `<span class="badge badge-success">Đang kích hoạt</span>`;
        case "INACTIVE":
            return `<span class="badge badge-secondary">Đã hết hạn</span>`;
        case "PENDING":
            return `<span class="badge badge-warning">Chưa kích hoạt</span>`;
        default:
            return `<span class="badge badge-danger">Unknown</span>`;
    }
}

// render discount value by discount type
const renderDiscountValue = (discountType, discountValue) => {
    switch (discountType) {
        case "PERCENT":
            return `${discountValue}%`;
        case "AMOUNT":
        case"SAME_PRICE":
            return `${formatCurrency(discountValue)}`;
        default:
            return `Unknown`;
    }
}

// Render tag
const tableContent = document.querySelector("table tbody")
const renderDiscountCampaings = (discountCampaingList) => {
    tableContent.innerHTML = "";
    let html = "";
    discountCampaingList.forEach((discountCampaing) => {
        html += `
                <tr>
                    <td>
                        <a href="/admin/discount-campaigns/${discountCampaing.campaignId}/detail">
                            ${discountCampaing.name}
                        </a>
                    </td>
                    <td>${generateStatus(discountCampaing.status)}</td>
                    <td>${discountCampaing.discountType}</td>
                    <td>${renderDiscountValue(discountCampaing.discountType, discountCampaing.discountValue)}</td>
                    <td>${formatDate(discountCampaing.startDate)} - ${formatDate(discountCampaing.endDate)}</td>
                    <td>${discountCampaing.products.length}</td>
                </tr>
                `
    })
    tableContent.innerHTML = html;
}

// render pagination using pagination.js
const renderPagination = () => {
    $('#pagination').pagination({
        dataSource: discountCampaigns,
        className: 'paginationjs-theme-blue paginationjs-big',
        callback: function (data, pagination) {
            renderDiscountCampaings(data);
        },
        hideOnlyOnePage: true
    })
}

renderPagination();