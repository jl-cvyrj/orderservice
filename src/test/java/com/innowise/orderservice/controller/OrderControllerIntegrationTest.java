package com.innowise.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.orderservice.dto.OrderDto;
import com.innowise.orderservice.dto.OrderItemDto;
import com.innowise.orderservice.entity.Item;
import com.innowise.orderservice.entity.OrderStatus;
import com.innowise.orderservice.repository.ItemRepository;
import com.innowise.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "USER_SERVICE_URL=http://localhost:8080"
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
class OrderControllerIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("test_order_db")
                    .withUsername("test_user")
                    .withPassword("test_pass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.liquibase.enabled", () -> "true");
    }

    private final MockMvc mockMvc;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public OrderControllerIntegrationTest(MockMvc mockMvc,
                                          OrderRepository orderRepository,
                                          ItemRepository itemRepository,
                                          ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void cleanUp() {
        orderRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void createOrder_ShouldSaveToDatabaseAndReturnCreated() throws Exception {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setPrice(new BigDecimal("10.00"));
        itemRepository.save(item);

        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setItemId(1L);
        orderItemDto.setQuantity((short) 1);

        List<OrderItemDto> items = new ArrayList<>();
        items.add(orderItemDto);

        OrderDto orderDto = new OrderDto();
        orderDto.setUserId(42L);
        orderDto.setStatus(OrderStatus.CREATED);
        orderDto.setItems(items);
        orderDto.setTotalPrice(new BigDecimal("100.00"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId", is(42)))
                .andExpect(jsonPath("$.status", is("CREATED")));

        assertEquals(1, orderRepository.count());
    }

    @Test
    void getOrderById_ShouldReturnNotFound_WhenOrderDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/orders/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}