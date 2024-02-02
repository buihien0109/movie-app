package com.example.movieapp.service;

import com.example.movieapp.entity.Order;
import com.example.movieapp.entity.User;
import com.example.movieapp.model.dto.FilmViewDto;
import com.example.movieapp.model.dto.RevenueDto;
import com.example.movieapp.repository.BlogRepository;
import com.example.movieapp.repository.FilmRepository;
import com.example.movieapp.repository.OrderRepository;
import com.example.movieapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    private final FilmRepository filmRepository;

    // Đếm số lượng các đơn hàng được tạo ra trong tháng hiện tại
    public long countLatestOrders() {
        Date start = Date.valueOf(LocalDate.now().withDayOfMonth(1));
        Date end = Date.valueOf(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));
        return orderRepository.countByCreatedAtBetween(start, end);
    }

    // Lấy danh sách các đơn hàng mới nhất trong tháng hiện tại sắp xếp theo ngày tạo giảm dần
    public List<Order> getLatestOrders(Integer limit) {
        Date start = Date.valueOf(LocalDate.now().withDayOfMonth(1));
        Date end = Date.valueOf(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));
        List<Order> orderList = orderRepository.findAllByCreatedAtBetweenOrderByCreatedAtDesc(start, end);
        if (orderList.size() > limit) {
            return orderList.subList(0, limit);
        }
        return orderList;
    }

    // Đếm số lượng các phim được tạo ra trong tháng hiện tại
    public long countLatestFilms() {
        Date start = Date.valueOf(LocalDate.now().withDayOfMonth(1));
        Date end = Date.valueOf(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));
        return filmRepository.countByCreatedAtBetween(start, end);
    }

    // Lấy danh sách các user mới nhất trong tháng hiện tại sắp xếp theo ngày tạo giảm dần
    public List<User> getLatestUsers(Integer limit) {
        Date start = Date.valueOf(LocalDate.now().withDayOfMonth(1));
        Date end = Date.valueOf(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));
        List<User> userList = userRepository.findAllByCreatedAtBetweenOrderByCreatedAtDesc(start, end);
        if (userList.size() > limit) {
            return userList.subList(0, limit);
        }
        return userList;
    }

    // Đếm số lượng các user được tạo ra trong tháng hiện tại và tổng số user có trong hệ thống (Map<String, Long>)
    public Map<String, Long> countLatestUsers() {
        Date start = Date.valueOf(LocalDate.now().withDayOfMonth(1));
        Date end = Date.valueOf(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));
        long count = userRepository.countByCreatedAtBetween(start, end);
        long total = userRepository.findAll().size();

        Map<String, Long> map = new HashMap<>();
        map.put("count", count);
        map.put("total", total);
        return map;
    }

    // Đếm số lượng các blog được tạo ra trong tháng hiện tại và tổng số blog có trong hệ thống (Map<String, Long>)
    public Map<String, Long> countLatestBlogs() {
        Date start = Date.valueOf(LocalDate.now().withDayOfMonth(1));
        Date end = Date.valueOf(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));
        long count = blogRepository.countByCreatedAtBetween(start, end);
        long total = blogRepository.findAll().size();

        Map<String, Long> map = new HashMap<>();
        map.put("count", count);
        map.put("total", total);
        return map;
    }

    // Lấy danh sách phim có lượt xem cao nhất trong tháng (sắp xếp theo lượt xem giảm dần)
    public List<FilmViewDto> getTopViewFilms(Integer limit) {
        Date start = Date.valueOf(LocalDate.now().withDayOfMonth(1));
        Date end = Date.valueOf(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));

        List<FilmViewDto> filmViewDtoList = filmRepository.findTopViewFilms(start, end);
        if (filmViewDtoList.size() > limit) {
            return filmViewDtoList.subList(0, limit);
        }
        return filmViewDtoList;
    }

    // Tính doanh 5 tháng gần nhất List<RevenueDto>
    public List<RevenueDto> getRevenueByMonth(Integer limit) {
        List<RevenueDto> renvenueDtoList = orderRepository.findRevenueByMonth();
        if (renvenueDtoList.size() > limit) {
            return renvenueDtoList.subList(0, limit);
        }
        return renvenueDtoList;
    }
}
