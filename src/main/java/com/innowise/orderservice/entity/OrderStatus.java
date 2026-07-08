package com.innowise.orderservice.entity;

public enum OrderStatus {

    CREATED,
    PAID,
    CONFIRMED,
    COMPLETED,
    CANCELLED,
    REFUNDED,
    PAYMENT_FAILED;

    public String getStatusLabel() {
        return "STATUS_" + this.name();
    }
}
