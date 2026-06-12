package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.dto.ItemDto;
import com.innowise.orderservice.entity.Item;
import com.innowise.orderservice.exception.ResourceNotFoundException;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.repository.ItemRepository;
import com.innowise.orderservice.service.ItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private static final String ITEM_NOT_FOUND_MESSAGE = "Item not found with id: ";

    private final ItemRepository itemRepository;
    private final OrderMapper itemMapper;

    public ItemServiceImpl(ItemRepository itemRepository, OrderMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
    }

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto) {

        Item item = itemMapper.toItem(itemDto);
        Item savedItem = itemRepository.save(item);
        return itemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto getItemById(Long id) {

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ITEM_NOT_FOUND_MESSAGE + id));
        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItems() {

        List<Item> items = itemRepository.findAll();
        return items.stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    @Transactional
    public ItemDto updateItemById(Long id, ItemDto updatedItemDto) {

        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ITEM_NOT_FOUND_MESSAGE + id));
        itemMapper.updateItemFromDto(updatedItemDto, existingItem);
        Item item = itemRepository.save(existingItem);
        return itemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public void deleteItemById(Long id) {

        itemRepository.deleteById(id);
    }
}
