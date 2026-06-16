package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.dto.ItemDto;
import com.innowise.orderservice.entity.Item;
import com.innowise.orderservice.exception.ResourceNotFoundException;
import com.innowise.orderservice.mapper.ItemMapper;
import com.innowise.orderservice.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setId(1L);

        itemDto = new ItemDto();
        itemDto.setId(1L);
    }

    @Test
    void createItem_Success() {
        when(itemMapper.toItem(itemDto)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.createItem(itemDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void getItemById_Success() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.getItemById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void getItemById_ThrowsResourceNotFoundException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> itemService.getItemById(1L));
        verify(itemRepository, times(1)).findById(1L);
        verifyNoInteractions(itemMapper);
    }

    @Test
    void getAllItems_Success() {
        List<Item> items = List.of(item);
        when(itemRepository.findAll()).thenReturn(items);
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        List<ItemDto> result = itemService.getAllItems();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void updateItemById_Success() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.updateItemById(1L, itemDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(itemMapper, times(1)).updateItemFromDto(itemDto, item);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void updateItemById_ThrowsResourceNotFoundException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> itemService.updateItemById(1L, itemDto));

        verify(itemRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(itemRepository);
        verifyNoInteractions(itemMapper);
    }

    @Test
    void deleteItemById_Success() {
        when(itemRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> itemService.deleteItemById(1L));
        verify(itemRepository, times(1)).deleteById(1L);
    }
}