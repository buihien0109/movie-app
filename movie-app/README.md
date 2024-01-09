## Web xem phim Netflix phiên bản Việt Nam (version 1)

### Sơ đồ thiết kế cơ sở dữ liệu

- https://dbdiagram.io/d/db-movie-app-659cc597ac844320ae80c2f9

### Màn hình quản trị (ADMIN)

- Trang đăng nhập : http://localhost:8080/dang-nhap

```
Role : ROLE_ADMIN
Username : admin@gmail.com
Password : 123

Role : ROLE_USER
Username : user@gmail.com
Password : 123
```

- Trang quản trị : http://localhost:8080/admin

Các chức năng trong trang quản trị:
  - Quản lý phim
  - Quản lý thể loại
  - Quản lý tin tức
  - Quản lý người dùng
  - Quản lý diễn viên
  - Quản lý đạo diễn

### Màn hình khách hàng

1. Trang chủ : http://localhost:8080
2. Trang phim lẻ : http://localhost:8080/phim-le
3. Trang phim bộ : http://localhost:8080/phim-bo
4. Trang phim chiếu rạp : http://localhost:8080/phim-chieu-rap
5. Trang chi tiết phim : http://localhost:8080/phim/115/trang-ti-phieu-luu-ky
6. Trang xem phim : http://localhost:8080/xem-phim/115/trang-ti-phieu-luu-ky?tap=full
7. Trang danh sách tin tức : http://localhost:8080/tin-tuc
8. Trang chi tiết tin tức : http://localhost:8080/tin-tuc/30/top-11-phim-co-doanh-thu-cao-nhat-moi-thoi-ai

### Một số hình ảnh minh họa trong trang web

#### 1. Trang chủ

![trang chủ film](../images/trang-chu.png)

#### 2. Phim lẻ

![phim lẻ](../images/phim-le.png)

#### 3. Phim bộ

![phim bộ](../images/phim-bo.png)

#### 4. Phim chiếu rạp

![chiếu rạp](../images/phim-chieu-rap.png)

#### 5. Phim chi tiết phim

![chi tiết phim](../images/chi-tiet.png)

#### 6. Tìm kiếm phim

![tìm kiếm](../images/tim-kiem.png)