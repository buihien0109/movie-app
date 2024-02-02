package com.example.movieapp;

import com.example.movieapp.model.dto.RevenueDto;
import com.example.movieapp.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class OrderTests {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void test_revenue() {
        List<RevenueDto> revenueDtoList = orderRepository.findRevenueByMonth();
        for (RevenueDto revenueDto : revenueDtoList) {
            System.out.println(revenueDto.getMonth() + "/" + revenueDto.getYear() + ": " + revenueDto.getRevenue());
        }
    }
}
