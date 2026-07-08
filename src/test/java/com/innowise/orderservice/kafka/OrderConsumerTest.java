package com.innowise.orderservice.kafka;

import com.innowise.orderservice.dto.event.PaymentEventDto;
import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.entity.OrderStatus;
import com.innowise.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

class OrderConsumerTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderConsumer orderConsumer;

    @Test
    void handlePaymentEvent_SuccessStatus_SetsPaid() {

        PaymentEventDto event = new PaymentEventDto("1", "SUCCESS");
        Order order = new Order();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        orderConsumer.handlePaymentEvent(event);

        assertEquals(OrderStatus.PAID, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void handlePaymentEvent_FailedStatus_SetsPaymentFailed() {
        PaymentEventDto event = new PaymentEventDto("1", "FAILED");
        Order order = new Order();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        orderConsumer.handlePaymentEvent(event);

        assertEquals(OrderStatus.PAYMENT_FAILED, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void handlePaymentEvent_UnknownStatus_SetsCancelled() {
        PaymentEventDto event = new PaymentEventDto("1", "OTHER");
        Order order = new Order();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        orderConsumer.handlePaymentEvent(event);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }
}