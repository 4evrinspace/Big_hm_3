package com.example.ordersservice.service;

import com.example.ordersservice.model.OutboxEvent;
import com.example.ordersservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxService {
    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 1000)
    public void processOutbox() {
        outboxEventRepository.findAllBySentFalse().forEach(event -> {
            kafkaTemplate.send(event.getType(), event.getPayload());
            event.setSent(true);
            outboxEventRepository.save(event);
        });
    }
} 