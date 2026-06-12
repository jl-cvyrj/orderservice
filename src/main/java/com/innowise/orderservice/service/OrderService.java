package com.innowise.orderservice.service;

import com.innowise.orderservice.dto.OrderDto;
import com.innowise.orderservice.entity.OrderStatus;
import com.innowise.orderservice.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

public interface OrderService {

    OrderDto createOrder(OrderDto orderDto);

    OrderDto getOrderById(Long id) throws ResourceNotFoundException;

    Page<OrderDto> getAllOrders(Instant start, Instant end, List<OrderStatus> statuses, Pageable pageable);

    List<OrderDto> getOrdersByUserId(Long userId);

    OrderDto updateOrderById(Long id, OrderDto updatedOrderDto) throws ResourceNotFoundException;

    void deleteOrderById(Long id) throws ResourceNotFoundException;
}
