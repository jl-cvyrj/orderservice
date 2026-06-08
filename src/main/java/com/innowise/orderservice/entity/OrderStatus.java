package com.innowise.orderservice.entity;

import org.springframework.security.core.GrantedAuthority;

public enum OrderStatus implements GrantedAuthority {

    CREATED,
    PAID,
    CONFIRMED,
    COMPLETED,
    CANCELLED,
    REFUNDED;

    @Override
    public String getAuthority() {
        return "STATUS_" + this.name();
    }
}
