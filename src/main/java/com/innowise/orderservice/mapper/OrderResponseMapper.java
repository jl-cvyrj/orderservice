package com.innowise.orderservice.mapper;

import com.innowise.orderservice.dto.OrderResponseDto;
import com.innowise.orderservice.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderResponseMapper {

    OrderResponseDto toOrderResponseDto(Order order);
}