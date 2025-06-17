package com.example.ordersservice.service;

import com.example.ordersservice.model.Order;
import com.example.ordersservice.model.OrderStatus;
import com.example.ordersservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.json.JSONObject;

@Service
@RequiredArgsConstructor
public class OrderStatusListener {
    private final OrderRepository orderRepository;

    @KafkaListener(topics = "payment-responses", groupId = "orders-service-group")
    public void handlePaymentResponse(String message) {
        JSONObject json = new JSONObject(message);
        Long orderId = json.getLong("orderId");
        String status = json.getString("status");
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return;
        if ("FINISHED".equals(status)) order.setStatus(OrderStatus.FINISHED);
        else order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
} 