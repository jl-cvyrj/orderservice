package com.innowise.orderservice.dto;

import com.innowise.orderservice.entity.OrderStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    private OrderStatus status;

    @DecimalMin(value = "0.01", message = "Price must be greater then or equal 0.01")
    @Digits(integer = 8, fraction = 2, message = "Price format must be up to 8 digits and 2 decimals")
    private BigDecimal totalPrice;

    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemDto> items;

    private UserDto user;
}
