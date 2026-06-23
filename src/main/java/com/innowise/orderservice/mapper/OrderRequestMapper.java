package com.innowise.orderservice.mapper;

import com.innowise.orderservice.dto.OrderRequestDto;
import com.innowise.orderservice.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderRequestMapper {

    Order toOrder(com.innowise.orderservice.dto.OrderRequestDto orderDto);
    void updateOrderFromDto(OrderRequestDto dto, @org.mapstruct.MappingTarget Order entity);
}