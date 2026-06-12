package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.client.UserClient;
import com.innowise.orderservice.dto.OrderDto;
import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.entity.OrderStatus;
import com.innowise.orderservice.exception.ResourceNotFoundException;
import com.innowise.orderservice.mapper.OrderMapper;
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
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private OrderDto orderDto;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setUserId(100L);
        order.setStatus(OrderStatus.CREATED);
        order.setTotalPrice(java.math.BigDecimal.ZERO);
        order.setDeleted(false);
        order.setItems(new ArrayList<>());

        orderDto = new OrderDto();
        orderDto.setId(1L);
        orderDto.setUserId(100L);
        orderDto.setStatus(OrderStatus.CREATED);
    }

    @Test
    void createOrder_Success() {
        when(orderMapper.toOrder(orderDto)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toOrderDto(order)).thenReturn(orderDto);

        OrderDto result = orderService.createOrder(orderDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void getOrderById_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toOrderDto(order)).thenReturn(orderDto);
        when(userClient.getUserById(100L)).thenReturn(null);

        OrderDto result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrderById_ThrowsResourceNotFoundException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(1L));
        verify(orderRepository, times(1)).findById(1L);
        verifyNoInteractions(orderMapper);
    }

    @Test
    void getAllOrders_Success() {
        Instant now = Instant.now();
        List<OrderStatus> statuses = List.of(OrderStatus.CREATED);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(List.of(order));

        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(orderPage);
        when(orderMapper.toOrderDto(order)).thenReturn(orderDto);
        when(userClient.getUserById(100L)).thenReturn(null);

        Page<OrderDto> result = orderService.getAllOrders(now, now, statuses, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(orderRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getOrdersByUserId_Success() {
        List<Order> orders = List.of(order);
        when(orderRepository.findByUserId(100L)).thenReturn(orders);
        when(orderMapper.toOrderDto(order)).thenReturn(orderDto);
        when(userClient.getUserById(100L)).thenReturn(null);

        List<OrderDto> result = orderService.getOrdersByUserId(100L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getUserId());
        verify(orderRepository, times(1)).findByUserId(100L);
    }

    @Test
    void updateOrderById_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toOrderDto(order)).thenReturn(orderDto);

        OrderDto result = orderService.updateOrderById(1L, orderDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderMapper, times(1)).updateOrderFromDto(orderDto, order);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void updateOrderById_ThrowsResourceNotFoundException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrderById(1L, orderDto));

        verify(orderRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(orderRepository);
        verifyNoInteractions(orderMapper);
    }

    @Test
    void deleteOrderById_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        assertDoesNotThrow(() -> orderService.deleteOrderById(1L));

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}