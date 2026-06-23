package com.innowise.orderservice.mapper;

import com.innowise.orderservice.dto.ItemDto;
import com.innowise.orderservice.entity.Item;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemDto toItemDto(Item item);
    Item toItem(ItemDto itemDto);

    void updateItemFromDto(ItemDto dto, @org.mapstruct.MappingTarget Item entity);
}
