// render blogs from blogs to body of table
const tableBody = document.querySelector('table tbody');
const renderBlogs = (blogList) => {
    let html = '';
    blogList.forEach(blog => {
        html += `
                    <tr>
                        <td>
                            <a href="/admin/blogs/${blog.id}/detail">${blog.title}</a>
                        </td>
                        <td>
                            <a href="/admin/users/${blog.user.userId}">${blog.user.username}</a>
                        </td>
                        <td>
                            ${blog.tags.map(tag => `<span class="badge bg-info mr-1">${tag.name}</span>`).join('')}
                        </td>
                        <td>${blog.status ? 'Công khai' : 'Nháp'}</td>
                        <td>${formatDate(blog.createdAt)}</td>
                    </tr>
                `;
    })
    tableBody.innerHTML = html;
}

// render pagination using pagination.js
const renderPagination = (blogList) => {
    $('#pagination').pagination({
        dataSource: blogList,
        className: 'paginationjs-theme-blue paginationjs-big',
        callback: function (data, pagination) {
            renderBlogs(data);
        },
        hideOnlyOnePage : true
    })
}
renderPagination(blogs);