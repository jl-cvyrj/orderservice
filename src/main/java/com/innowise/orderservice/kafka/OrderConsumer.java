package com.innowise.orderservice.kafka;

import com.innowise.orderservice.dto.event.PaymentEventDto;
import com.innowise.orderservice.entity.OrderStatus;
import com.innowise.orderservice.repository.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {

    private final OrderRepository orderRepository;

    public OrderConsumer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @KafkaListener(topics = "payment-events", groupId = "order-group")
    public void handlePaymentEvent(PaymentEventDto event) {
        try {
            Long orderId = Long.parseLong(event.orderId());

            orderRepository.findById(orderId).ifPresent(order -> {
                OrderStatus status = mapStatus(event.status());
                order.setStatus(status);
                orderRepository.save(order);
            });
        } catch (NumberFormatException e) {
            System.err.println("Invalid order ID received: " + event.orderId());
        } catch (Exception e) {
            System.err.println("Error processing payment event: " + e.getMessage());
        }
    }

    private OrderStatus mapStatus(String paymentStatus) {
        return switch (paymentStatus) {
            case "SUCCESS" -> OrderStatus.PAID;
            case "FAILED" -> OrderStatus.PAYMENT_FAILED;
            default -> OrderStatus.CANCELLED;
        };
    }
}