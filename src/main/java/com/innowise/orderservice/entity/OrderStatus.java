package com.innowise.orderservice.entity;

public enum OrderStatus {

    CREATED,
    PAID,
    CONFIRMED,
    COMPLETED,
    CANCELLED,
    REFUNDED;

    public String getStatusLabel() {
        return "STATUS_" + this.name();
    }
}
