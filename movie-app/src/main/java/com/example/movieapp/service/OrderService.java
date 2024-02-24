package com.example.movieapp.service;

import com.example.movieapp.entity.Film;
import com.example.movieapp.entity.Order;
import com.example.movieapp.entity.User;
import com.example.movieapp.exception.BadRequestException;
import com.example.movieapp.exception.ResourceNotFoundException;
import com.example.movieapp.model.dto.OrderDto;
import com.example.movieapp.model.enums.FilmAccessType;
import com.example.movieapp.model.enums.OrderStatus;
import com.example.movieapp.model.request.AdminCreateOrderRequest;
import com.example.movieapp.model.request.CreateOrderRequest;
import com.example.movieapp.model.request.UpdateOrderRequest;
import com.example.movieapp.repository.FilmRepository;
import com.example.movieapp.repository.OrderRepository;
import com.example.movieapp.repository.UserRepository;
import com.example.movieapp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    public List<OrderDto> getOrdersOfCurrentUser() {
        User user = SecurityUtils.getCurrentUserLogin();
        return orderRepository.findByUser_Id(user.getId(), Sort.by("createdAt").descending());
    }

    public Order getOrderById(Integer id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
    }

    // admin get all blogs
    @Transactional
    public Map<String, Object> getAllOrders(int start, int length, int draw, int sortColumn, String sortOrder) {
        log.info("start: {}, length: {}, draw: {}, sortColumn: {}, sortOrder: {}", start, length, draw, sortColumn, sortOrder);

        Sort sort = getSort(sortColumn, sortOrder);
        Pageable pageable = PageRequest.of(start / length, length, sort);
        Page<Order> pageData = orderRepository.findAll(pageable);

        Map<String, Object> data = new HashMap<>();
        data.put("draw", draw);
        data.put("recordsTotal", pageData.getTotalElements());
        data.put("recordsFiltered", pageData.getTotalElements());
        data.put("data", pageData.getContent());

        return data;
    }

    private Sort getSort(int column, String dir) {
        String sortField = switch (column) {
            case 0 -> "id";
            case 1 -> "user.name";
            case 2 -> "createdAt";
            case 3 -> "status";
            case 4 -> "paymentMethod";
            case 5 -> "film.title";
            case 6 -> "amount";
            default -> "createdAt"; // Mặc định sắp xếp theo title
            // Các trường hợp khác tùy theo cấu trúc của bảng
        };
        return Sort.by(Sort.Direction.fromString(dir), sortField);
    }

    public Order createOrderByCustomer(CreateOrderRequest request) {
        log.info("request: {}", request);
        // Kiểm tra xem phim có tồn tại hay không
        Film film = filmRepository.findByIdAndAccessType(request.getFilmId(), FilmAccessType.PAID)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim"));

        // Kiểm tra xem người dùng đã mua phim này chưa
        User user = SecurityUtils.getCurrentUserLogin();
        if (orderRepository.existsByUser_IdAndFilm_IdAndStatus(user.getId(), film.getId(), OrderStatus.SUCCESS)) {
            throw new BadRequestException("Người dùng đã mua phim này rồi");
        }

        // Tạo mới order
        Order order = Order.builder()
                .user(user)
                .film(film)
                .amount(film.getPrice())
                .status(OrderStatus.PENDING)
                .paymentMethod(request.getPaymentMethod())
                .build();

        // Lưu vào database
        orderRepository.save(order);

        // Gửi mail xác nhận đặt hàng
        Map<String, Object> data = new HashMap<>();
        data.put("email", user.getEmail());
        data.put("user", user);
        data.put("order", order);
        mailService.sendMailConfirmOrder(data);
        log.info("Email sent to: {}", user.getEmail());

        return order;
    }

    public Order updateOrderByAdmin(Integer id, UpdateOrderRequest request) {
        log.info("id: {}, request: {}", id, request);
        // Kiểm tra xem id có tồn tại hay không
        Order order = getOrderById(id);

        // Kiểm tra phim trong request có tồn tại và là phim trả phí hay không
        Film film = filmRepository.findByIdAndAccessType(request.getFilmId(), FilmAccessType.PAID)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim"));

        if (request.getStatus() == OrderStatus.SUCCESS && order.getStatus() != OrderStatus.SUCCESS) {
            // Lấy danh sách order của user và phim
            List<Order> orderList = orderRepository
                    .findByUser_IdAndFilm_IdAndFilm_AccessType(order.getUser().getId(), film.getId(), FilmAccessType.PAID);

            // Nếu danh sách order có size > 0 và trong danh sách order đó có order khác id của order hiện tại và status = SUCCESS -> throw exception
            if (!orderList.isEmpty() && orderList.stream().anyMatch(o -> !o.getId().equals(id) && o.getStatus().equals(OrderStatus.SUCCESS))) {
                throw new BadRequestException("Người dùng đã mua phim này rồi");
            }
        }

        // Câp nhật dữ liệu
        order.setFilm(film);
        order.setAmount(request.getAmount());
        order.setStatus(request.getStatus());
        order.setNote(request.getNote());
        order.setPaymentMethod(request.getPaymentMethod());

        // Lưu vào database
        return orderRepository.save(order);
    }

    // Kiểm tra xem user đã mua phim này chưa? (OrderStatus.SUCCESS) -> return boolean
    public boolean checkUserBoughtFilm(Integer filmId) {
        User user = SecurityUtils.getCurrentUserLogin();
        if (user == null) {
            return false;
        }
        return orderRepository.existsByUser_IdAndFilm_IdAndStatus(user.getId(), filmId, OrderStatus.SUCCESS);
    }

    public Order createOrderByAdmin(AdminCreateOrderRequest request) {
        log.info("request: {}", request);

        // Kiểm tra user có tồn tại hay không
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));

        // Kiểm tra xem phim có tồn tại hay không
        Film film = filmRepository.findByIdAndAccessType(request.getFilmId(), FilmAccessType.PAID)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim"));

        // Kiểm tra xem người dùng đã mua phim này chưa
        if (orderRepository.existsByUser_IdAndFilm_IdAndStatus(user.getId(), film.getId(), OrderStatus.SUCCESS)) {
            throw new BadRequestException("Người dùng đã mua phim này rồi");
        }

        // Tạo mới order
        Order order = Order.builder()
                .user(user)
                .film(film)
                .amount(film.getPrice())
                .status(OrderStatus.PENDING)
                .paymentMethod(request.getPaymentMethod())
                .build();

        // Lưu vào database
        return orderRepository.save(order);
    }
}
