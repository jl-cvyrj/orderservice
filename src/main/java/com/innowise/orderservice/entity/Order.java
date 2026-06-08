package com.innowise.orderservice.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Getter
    private Long id;

    @Column(name = "user_id", nullable = false)
    @Getter
    @Setter
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Getter
    @Setter
    private OrderStatus status;

    @Column(name = "total_price", nullable = false)
    @Getter
    @Setter
    private BigDecimal totalPrice;

    @Column(name = "deleted", nullable = false)
    @Getter
    @Setter
    private boolean deleted;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    @Getter
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @Getter
    private Instant updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    private final List<OrderItem> items = new ArrayList<>();

    protected Order() {}

    public Order(Long userId, OrderStatus status, BigDecimal totalPrice, boolean deleted) {
        this.userId = userId;
        this.status = status;
        this.totalPrice = totalPrice;
        this.deleted = deleted;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
        item.setOrder(this);
    }
}