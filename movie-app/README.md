## Web xem phim Netflix phiên bản Việt Nam (version 2)

Link demo: http://103.237.147.34:8888/

### Triển khai ứng dụng

Sau khi clone source về máy, truy cập vào thư mục `movie-app` chứa source code

```
cd movie-app
```

Sử dụng docker-compose để triển khai ứng dụng

```
docker-compose up -d
```

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

### Github Action

```yaml
name: Java CI/CD Pipeline

on:
  push:
    branches:
      - main

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v2

    - name: Set up JDK 17
      uses: actions/setup-java@v2
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Build with Maven
      working-directory: ./movie-app
      run: |
        echo "Maven version:"
        mvn -version
        mvn -B clean install

    - name: Build Docker Image
      working-directory: ./movie-app
      run: docker build -t buihien0109/movie-app:latest .

    - name: Login to Docker Hub
      run: echo ${{ secrets.DOCKER_PASSWORD }} | docker login -u ${{ secrets.DOCKER_USERNAME }} --password-stdin

    - name: Push Docker Image to Docker Hub
      run: docker push buihien0109/movie-app:latest
```