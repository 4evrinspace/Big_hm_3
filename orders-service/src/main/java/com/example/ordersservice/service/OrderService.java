package com.example.ordersservice.service;

import com.example.ordersservice.model.Order;
import com.example.ordersservice.model.OrderStatus;
import com.example.ordersservice.model.OutboxEvent;
import com.example.ordersservice.repository.OrderRepository;
import com.example.ordersservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Order> kafkaTemplate;

    @Transactional
    public Order createOrder(Order order) {
        order.setStatus(OrderStatus.CREATED);
        Order savedOrder = orderRepository.save(order);
        OutboxEvent event = new OutboxEvent();
        event.setAggregateType("Order");
        event.setAggregateId(savedOrder.getId().toString());
        event.setType("payment-requests");
        event.setPayload("{\"orderId\":" + savedOrder.getId() + ",\"userId\":\"" + savedOrder.getUserId() + "\",\"amount\":" + savedOrder.getAmount() + "}");
        outboxEventRepository.save(event);
        return savedOrder;
    }

    public void processPaymentResult(Long orderId, boolean paymentSuccessful) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(paymentSuccessful ? OrderStatus.FINISHED : OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
} 