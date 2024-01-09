// render blogs from blogs to body of table
const tableBody = document.querySelector('table tbody');
const renderBanners = (bannerList) => {
    let html = '';
    bannerList.forEach(banner => {
        html += `
                  <tr>
                      <td>
                          <a href="/admin/banners/${banner.id}/detail">${banner.name}</a>
                      </td>
                      <td>
                          ${banner.status ? `<span class="badge badge-success">Kích hoạt</span>` : `<span class="badge badge-secondary">Vô hiệu hóa</span>`}
                      </td>
                      <td>
                          <a href="${banner.linkRedirect}">${banner.linkRedirect}</a>
                      </td>
                      <td>${formatDate(banner.createdAt)}</td>
                  </tr>
              `;
    })
    tableBody.innerHTML = html;
}

// render pagination using pagination.js
const renderPagination = () => {
    $('#pagination').pagination({
        dataSource: banners,
        className: 'paginationjs-theme-blue paginationjs-big',
        callback: function (data, pagination) {
            renderBanners(data);
        },
        hideOnlyOnePage: true
    })
}
renderPagination();