package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.client.UserClient;
import com.innowise.orderservice.dto.OrderRequestDto;
import com.innowise.orderservice.dto.OrderResponseDto;
import com.innowise.orderservice.dto.UserDto;
import com.innowise.orderservice.entity.Item;
import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.entity.OrderItem;
import com.innowise.orderservice.entity.OrderStatus;
import com.innowise.orderservice.exception.ResourceNotFoundException;
import com.innowise.orderservice.mapper.OrderRequestMapper;
import com.innowise.orderservice.mapper.OrderResponseMapper;
import com.innowise.orderservice.repository.ItemRepository;
import com.innowise.orderservice.repository.OrderRepository;
import com.innowise.orderservice.service.OrderService;
import com.innowise.orderservice.specification.OrderSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger log = LogManager.getLogger(OrderServiceImpl.class);
    private static final String ORDER_NOT_FOUND_MESSAGE = "Order not found with id: ";
    private static final String USER_NOT_FOUND_MESSAGE = "Failed to fetch user";

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final OrderRequestMapper orderRequestMapper;
    private final OrderResponseMapper orderResponseMapper;
    private final UserClient userClient;

    public OrderServiceImpl(OrderRepository orderRepository, ItemRepository itemRepository, OrderRequestMapper orderMapper, OrderResponseMapper orderResponseMapper, UserClient userClient) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.orderRequestMapper = orderMapper;
        this.orderResponseMapper = orderResponseMapper;
        this.userClient = userClient;
    }

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto orderDto) {
        Order order = orderRequestMapper.toOrder(orderDto);
        order.setStatus(OrderStatus.CREATED);
        linkOrderItems(order);

        Order savedOrder = orderRepository.save(order);
        OrderResponseDto responseDto = orderResponseMapper.toOrderResponseDto(savedOrder);

        enrichOrderWithUser(responseDto, savedOrder.getUserId());
        return responseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MESSAGE + id));

        OrderResponseDto orderDto = orderResponseMapper.toOrderResponseDto(order);
        enrichOrderWithUser(orderDto, order.getUserId());

        return orderDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getAllOrders(Instant start, Instant end, List<OrderStatus> statuses, Pageable pageable) {

        Specification<Order> specification = Specification
                .where(OrderSpecification.createdBetween(start, end))
                .and(OrderSpecification.hasStatus(statuses));

        Page<Order> orderPage = orderRepository.findAll(specification, pageable);
        return orderPage.map(order -> {
            OrderResponseDto orderDto = orderResponseMapper.toOrderResponseDto(order);
            enrichOrderWithUser(orderDto, order.getUserId());
            return orderDto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByUserId(Long userId) {

        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(order -> {
                    OrderResponseDto orderDto = orderResponseMapper.toOrderResponseDto(order);
                    enrichOrderWithUser(orderDto, order.getUserId());
                    return orderDto;
                })
                .toList();
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrderById(Long id, OrderRequestDto updatedOrderDto) {

        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MESSAGE + id));
        orderRequestMapper.updateOrderFromDto(updatedOrderDto, existingOrder);

        linkOrderItems(existingOrder);

        Order savedOrder = orderRepository.save(existingOrder);
        OrderResponseDto responseOrderDto = orderResponseMapper.toOrderResponseDto(savedOrder);
        enrichOrderWithUser(responseOrderDto, savedOrder.getUserId());

        return responseOrderDto;
    }

    @Override
    @Transactional
    public void deleteOrderById(Long id) {

        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MESSAGE + id));

        orderRepository.delete(existingOrder);
    }

    private void linkOrderItems(Order order) {
        List<OrderItem> itemsMapper = order.getItems();
        order.setItems(new ArrayList<>());

        if(itemsMapper != null) {
            for (OrderItem orderItem : itemsMapper) {
                if (orderItem.getItem() == null && orderItem.getItemId() != null) {
                    Item item = itemRepository.findById(orderItem.getItemId())
                            .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
                    orderItem.setItem(item);
                }
                order.addItem(orderItem);
            }
        }
    }

    private void enrichOrderWithUser(OrderResponseDto orderDto, Long userId) {
        if (userId != null) {
            try {
                UserDto userDto = userClient.getUserById(userId);
                orderDto.setUser(userDto);
            } catch (Exception e) {
                log.error(USER_NOT_FOUND_MESSAGE, e);
            }
        }
    }
}
