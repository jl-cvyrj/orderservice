package com.innowise.orderservice.dto.event;

public record PaymentEventDto(String orderId, String status) {}
