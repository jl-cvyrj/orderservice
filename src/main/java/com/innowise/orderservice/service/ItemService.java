package com.innowise.orderservice.service;

import com.innowise.orderservice.dto.ItemDto;
import com.innowise.orderservice.exception.ResourceNotFoundException;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto);

    ItemDto getItemById(Long id) throws ResourceNotFoundException;

    List<ItemDto> getAllItems();

    ItemDto updateItemById(Long id, ItemDto updatedItemDto) throws ResourceNotFoundException;

    void deleteItemById(Long id) throws ResourceNotFoundException;
}
