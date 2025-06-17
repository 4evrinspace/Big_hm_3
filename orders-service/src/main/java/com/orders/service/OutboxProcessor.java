package com.orders.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orders.model.OutboxMessage;
import com.orders.repository.OutboxMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxProcessor {
    private final OutboxMessageRepository outboxMessageRepository;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 1000)
    @Transactional
    public void processOutboxMessages() {
        List<OutboxMessage> messages = outboxMessageRepository.findByProcessedFalse();
        for (OutboxMessage message : messages) {
            try {
                // TODO: Реализовать отправку в Kafka
                message.setProcessed(true);
                outboxMessageRepository.save(message);
            } catch (Exception e) {
                // Log error and continue with next message
            }
        }
    }
} 