package com.example.movieapp.repository;

import com.example.movieapp.entity.Order;
import com.example.movieapp.model.dto.RevenueDto;
import com.example.movieapp.model.enums.FilmAccessType;
import com.example.movieapp.model.enums.OrderStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUser_Id(Integer id, Sort sort);

    List<Order> findAllByCreatedAtBetweenOrderByCreatedAtDesc(Date start, Date end);

    long countByCreatedAtBetween(Date start, Date end);

    // find revenue by month latest calcalate by sum total price of order has status = 'SUCCESS' and group by month and year and order by year desc and month desc
    @Query(value = "SELECT new com.example.movieapp.model.dto.RevenueDto(MONTH(o.createdAt), YEAR(o.createdAt), SUM(o.amount)) FROM Order o WHERE o.status = 'SUCCESS' GROUP BY MONTH(o.createdAt), YEAR(o.createdAt) ORDER BY YEAR(o.createdAt) ASC, MONTH(o.createdAt) ASC")
    List<RevenueDto> findRevenueByMonth();

    Optional<Order> findByUser_IdAndFilm_IdAndFilm_AccessType(Integer userId, Integer filmId, FilmAccessType filmAccessType);

    boolean existsByUser_IdAndFilm_IdAndStatus(Integer userId, Integer filmId, OrderStatus orderStatus);
}