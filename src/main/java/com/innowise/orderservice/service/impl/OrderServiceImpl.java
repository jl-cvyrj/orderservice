package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.dto.OrderDto;
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

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {

        Order order = orderMapper.toOrder(orderDto);
        order.setStatus(OrderStatus.CREATED);

        linkOrderItems(order);

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toOrderDto(savedOrder);
    }

    @Override
    public OrderDto getOrderById(Long id) throws ResourceNotFoundException {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MESSAGE + id));
        return orderMapper.toOrderDto(order);
    }

    @Override
    public Page<OrderDto> getAllOrders(Instant start, Instant end, List<OrderStatus> statuses, Pageable pageable) {

        Specification<Order> specification = Specification
                .where(OrderSpecification.createdBetween(start, end))
                .and(OrderSpecification.hasStatus(statuses));

        Page<Order> orderPage = orderRepository.findAll(specification, pageable);
        return orderPage.map(orderMapper::toOrderDto);
    }

    @Override
    public List<OrderDto> getOrdersByUserId(Long userId) {

        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(orderMapper::toOrderDto)
                .toList();
    }

    @Override
    @Transactional
    public OrderDto updateOrderById(Long id, OrderDto updatedOrderDto) throws ResourceNotFoundException {

        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MESSAGE + id));
        orderMapper.updateOrderFromDto(updatedOrderDto, existingOrder);

        linkOrderItems(existingOrder);

        Order savedOrder = orderRepository.save(existingOrder);
        return orderMapper.toOrderDto(savedOrder);
    }

    @Override
    @Transactional
    public void deleteOrderById(Long id) throws ResourceNotFoundException {

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
