package com.example.movieapp.rest;

import com.example.movieapp.model.request.UpdateOrderRequest;
import com.example.movieapp.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class OrderResource {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<?> getAllBlogs(@RequestParam("start") int start,
                                         @RequestParam("length") int length,
                                         @RequestParam("draw") int draw,
                                         @RequestParam("order[0][column]") int sortColumn,
                                         @RequestParam("order[0][dir]") String sortOrder) {
        return ResponseEntity.ok(orderService.getAllOrders(start, length, draw, sortColumn, sortOrder));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Integer id, @Valid @RequestBody UpdateOrderRequest request) {
        return ResponseEntity.ok(orderService.updateOrder(id, request));
    }
}
