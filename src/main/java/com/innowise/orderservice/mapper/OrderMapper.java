package com.innowise.orderservice.mapper;

import com.innowise.orderservice.dto.ItemDto;
import com.innowise.orderservice.dto.OrderDto;
import com.innowise.orderservice.dto.OrderItemDto;
import com.innowise.orderservice.entity.Item;
import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    ItemDto toItemDto(Item item);
    Item toItem(ItemDto itemDto);

    @Mapping(target = "itemId", source = "item.id")
    OrderItemDto toOrderItemDto(OrderItem orderItem);

    @Mapping(target = "item.id", source = "itemId")
    OrderItem toOrderItem(OrderItemDto orderItemDto);

    OrderDto toOrderDto(Order order);
    Order toOrder(OrderDto orderDto);
}
