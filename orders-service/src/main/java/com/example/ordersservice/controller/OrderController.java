package com.example.ordersservice.controller;

import com.example.ordersservice.model.Order;
import com.example.ordersservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }

    @PostMapping("/{orderId}/payment-result")
    public void processPaymentResult(@PathVariable Long orderId, @RequestParam boolean success) {
        orderService.processPaymentResult(orderId, success);
    }
} 