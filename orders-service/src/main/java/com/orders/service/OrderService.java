package com.orders.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orders.config.KafkaConfig;
import com.orders.model.Order;
import com.orders.model.OutboxMessage;
import com.orders.repository.OrderRepository;
import com.orders.repository.OutboxMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OutboxMessageRepository outboxMessageRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public Order createOrder(Long userId, Double amount) {
        Order order = new Order();
        order.setUserId(userId);
        order.setAmount(amount);
        order = orderRepository.save(order);

        // Create outbox message for payment request
        OutboxMessage outboxMessage = new OutboxMessage();
        outboxMessage.setEventType("PAYMENT_REQUEST");
        outboxMessage.setPayload(String.format("{\"orderId\":%d,\"userId\":%d,\"amount\":%f}", 
            order.getId(), userId, amount));
        outboxMessageRepository.save(outboxMessage);

        // Send payment request to Kafka
        kafkaTemplate.send(KafkaConfig.PAYMENT_REQUEST_TOPIC, 
            String.format("{\"orderId\":%d,\"userId\":%d,\"amount\":%f}", 
                order.getId(), userId, amount));

        return order;
    }

    public List<Order> getOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
    }
} 