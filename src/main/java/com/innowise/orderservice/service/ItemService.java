package com.innowise.orderservice.service;

import com.innowise.orderservice.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto);

    ItemDto getItemById(Long id);

    List<ItemDto> getAllItems();

    ItemDto updateItemById(Long id, ItemDto updatedItemDto);

    void deleteItemById(Long id);
}
