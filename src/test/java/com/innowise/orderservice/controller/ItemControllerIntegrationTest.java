package com.innowise.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.orderservice.dto.ItemDto;
import com.innowise.orderservice.repository.ItemRepository;
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

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class ItemControllerIntegrationTest {

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
    private final ItemRepository itemRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public ItemControllerIntegrationTest(MockMvc mockMvc,
                                         ItemRepository itemRepository,
                                         ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.itemRepository = itemRepository;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void cleanUp() {
        itemRepository.deleteAll();
    }

    @Test
    void createItem_ShouldSaveToDatabaseAndReturnCreated() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Product");
        itemDto.setPrice(new BigDecimal("99.99"));

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.price", is(99.99)));

        assertEquals(1, itemRepository.count());
    }

    @Test
    void getItemById_ShouldReturnNotFound_WhenItemDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/items/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}