package com.innowise.orderservice.kafka;

import com.innowise.orderservice.dto.event.PaymentEventDto;
import com.innowise.orderservice.entity.OrderStatus;
import com.innowise.orderservice.repository.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {

    private final OrderRepository orderRepository;

    public OrderConsumer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @KafkaListener(topics = "payment-events", groupId = "order-group")
    public void handlePaymentEvent(PaymentEventDto event) {
        orderRepository.findById(Long.valueOf(event.orderId())).ifPresent(order -> {
            String newStatus = event.status().equals("SUCCESS") ? "PAID" : "CANCELLED";
            order.setStatus(OrderStatus.valueOf(newStatus));
            orderRepository.save(order);
        });
    }
}