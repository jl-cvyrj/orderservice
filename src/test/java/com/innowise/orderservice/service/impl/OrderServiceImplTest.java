package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.client.UserClient;
import com.innowise.orderservice.dto.OrderRequestDto;
import com.innowise.orderservice.dto.OrderResponseDto;
import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.entity.OrderStatus;
import com.innowise.orderservice.exception.ResourceNotFoundException;
import com.innowise.orderservice.mapper.OrderRequestMapper;
import com.innowise.orderservice.mapper.OrderResponseMapper;
import com.innowise.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderRequestMapper orderRequestMapper;

    @Mock
    private OrderResponseMapper orderResponseMapper;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private OrderRequestDto orderRequestDto;
    private OrderResponseDto orderResponseDto;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setUserId(100L);
        order.setStatus(OrderStatus.CREATED);
        order.setItems(new ArrayList<>());

        orderRequestDto = new OrderRequestDto();
        orderRequestDto.setUserId(100L);

        orderResponseDto = new OrderResponseDto();
        orderResponseDto.setId(1L);
    }

    @Test
    void createOrder_Success() {
        when(orderRequestMapper.toOrder(orderRequestDto)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);
        when(orderResponseMapper.toOrderResponseDto(order)).thenReturn(orderResponseDto);

        OrderResponseDto result = orderService.createOrder(orderRequestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void getOrderById_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderResponseMapper.toOrderResponseDto(order)).thenReturn(orderResponseDto);

        OrderResponseDto result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrderById_ThrowsResourceNotFoundException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(1L));
        verify(orderRepository, times(1)).findById(1L);
        verifyNoInteractions(orderResponseMapper);
    }

    @Test
    void getAllOrders_Success() {
        Instant now = Instant.now();
        List<OrderStatus> statuses = List.of(OrderStatus.CREATED);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(List.of(order));

        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(orderPage);
        when(orderResponseMapper.toOrderResponseDto(order)).thenReturn(orderResponseDto);

        Page<OrderResponseDto> result = orderService.getAllOrders(now, now, statuses, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(orderRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getOrdersByUserId_Success() {
        List<Order> orders = List.of(order);
        when(orderRepository.findByUserId(100L)).thenReturn(orders);
        when(orderResponseMapper.toOrderResponseDto(order)).thenReturn(orderResponseDto);

        List<OrderResponseDto> result = orderService.getOrdersByUserId(100L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findByUserId(100L);
    }

    @Test
    void updateOrderById_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderResponseMapper.toOrderResponseDto(order)).thenReturn(orderResponseDto);

        OrderResponseDto result = orderService.updateOrderById(1L, orderRequestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderRequestMapper, times(1)).updateOrderFromDto(orderRequestDto, order);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void deleteOrderById_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertDoesNotThrow(() -> orderService.deleteOrderById(1L));

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).delete(order);
    }
}