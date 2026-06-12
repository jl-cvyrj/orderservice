package com.innowise.orderservice.controller;

import com.innowise.orderservice.dto.OrderDto;
import com.innowise.orderservice.entity.OrderStatus;
import com.innowise.orderservice.exception.ResourceNotFoundException;
import com.innowise.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody OrderDto orderDto) {
        OrderDto createdOrder = orderService.createOrder(orderDto);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        OrderDto orderDto = orderService.getOrderById(id);
        return ResponseEntity.ok(orderDto);
    }

    @GetMapping
    public ResponseEntity<Page<OrderDto>> getAllOrders(
            @RequestParam(required = false) Instant start,
            @RequestParam(required = false) Instant end,
            @RequestParam(required = false) List<OrderStatus> statuses,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<OrderDto> orderDtoPage = orderService.getAllOrders(start, end, statuses, pageable);
        return ResponseEntity.ok(orderDtoPage);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDto>> getOrdersByUserId(@PathVariable Long userId) {
        List<OrderDto> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrderById(
            @PathVariable Long id,
            @Valid @RequestBody OrderDto updatedOrderDto) {

        OrderDto updatedOrder = orderService.updateOrderById(id, updatedOrderDto);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderById(@PathVariable Long id) {
        orderService.deleteOrderById(id);
        return ResponseEntity.noContent().build();
    }
}

