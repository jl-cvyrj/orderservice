package com.innowise.orderservice.mapper;

import com.innowise.orderservice.dto.OrderItemDto;
import com.innowise.orderservice.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "itemId", source = "item.id")
    OrderItemDto toOrderItemDto(OrderItem orderItem);

    @Mapping(target = "item.id", source = "itemId")
    OrderItem toOrderItem(OrderItemDto orderItemDto);
}
