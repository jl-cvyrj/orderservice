package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.client.UserClient;
import com.innowise.orderservice.dto.OrderDto;
import com.innowise.orderservice.dto.UserDto;
import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.entity.OrderItem;
import com.innowise.orderservice.entity.OrderStatus;
import com.innowise.orderservice.exception.ResourceNotFoundException;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.repository.OrderRepository;
import com.innowise.orderservice.service.OrderService;
import com.innowise.orderservice.specification.OrderSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final String ORDER_NOT_FOUND_MESSAGE = "Order not found with id: ";

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserClient userClient;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper, UserClient userClient) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.userClient = userClient;
    }

    @Override
    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {

        Order order = orderMapper.toOrder(orderDto);
        order.setStatus(OrderStatus.CREATED);

        linkOrderItems(order);

        Order savedOrder = orderRepository.save(order);
        OrderDto responseDto = orderMapper.toOrderDto(savedOrder);

        if (savedOrder.getUserId() != null) {
            UserDto userDto = userClient.getUserById(savedOrder.getUserId());
            responseDto.setUser(userDto);
        }

        return responseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MESSAGE + id));
        OrderDto orderDto = orderMapper.toOrderDto(order);

        if (order.getUserId() != null) {
            UserDto userDto = userClient.getUserById(order.getUserId());
            orderDto.setUser(userDto);
        }

        return orderDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getAllOrders(Instant start, Instant end, List<OrderStatus> statuses, Pageable pageable) {

        Specification<Order> specification = Specification
                .where(OrderSpecification.createdBetween(start, end))
                .and(OrderSpecification.hasStatus(statuses));

        Page<Order> orderPage = orderRepository.findAll(specification, pageable);
        return orderPage.map(order -> {
            OrderDto orderDto = orderMapper.toOrderDto(order);
            if(order.getUserId() != null) {
                UserDto userDto = userClient.getUserById(order.getUserId());
                orderDto.setUser(userDto);
            }
            return orderDto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByUserId(Long userId) {

        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(order -> {
                    OrderDto orderDto = orderMapper.toOrderDto(order);
                    if(order.getUserId() != null) {
                        UserDto userDto = userClient.getUserById(order.getUserId());
                        orderDto.setUser(userDto);
                    }
                    return orderDto;
                })
                .toList();
    }

    @Override
    @Transactional
    public OrderDto updateOrderById(Long id, OrderDto updatedOrderDto) {

        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MESSAGE + id));
        orderMapper.updateOrderFromDto(updatedOrderDto, existingOrder);

        linkOrderItems(existingOrder);

        Order savedOrder = orderRepository.save(existingOrder);
        OrderDto responseOrderDto = orderMapper.toOrderDto(savedOrder);

        if(savedOrder.getUserId() != null) {
            UserDto userDto = userClient.getUserById(savedOrder.getUserId());
            responseOrderDto.setUser(userDto);
        }
        return responseOrderDto;
    }

    @Override
    @Transactional
    public void deleteOrderById(Long id) {

        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MESSAGE + id));

        existingOrder.setDeleted(true);
        orderRepository.save(existingOrder);
    }

    public void linkOrderItems(Order order) {
        List<OrderItem> itemsMapper = order.getItems();
        order.setItems(new ArrayList<>());

        if(itemsMapper != null) {
            for (OrderItem item : itemsMapper) {
                order.addItem(item);
            }
        }
    }
}
