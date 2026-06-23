package com.innowise.orderservice.service;

import com.innowise.orderservice.dto.OrderRequestDto;
import com.innowise.orderservice.dto.OrderResponseDto;
import com.innowise.orderservice.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

public interface OrderService {

    OrderResponseDto createOrder(OrderRequestDto orderDto);

    OrderResponseDto getOrderById(Long id);

    Page<OrderResponseDto> getAllOrders(Instant start, Instant end, List<OrderStatus> statuses, Pageable pageable);

    List<OrderResponseDto> getOrdersByUserId(Long userId);

    OrderResponseDto updateOrderById(Long id, OrderRequestDto updatedOrderDto);

    void deleteOrderById(Long id);
}
