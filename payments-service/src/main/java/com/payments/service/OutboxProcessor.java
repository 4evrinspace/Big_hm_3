package com.payments.service;

import com.payments.model.OutboxMessage;
import com.payments.repository.OutboxMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxProcessor {
    private final OutboxMessageRepository outboxMessageRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedRate = 1000)
    @Transactional
    public void processOutboxMessages() {
        List<OutboxMessage> messages = outboxMessageRepository.findByProcessedFalse();
        for (OutboxMessage message : messages) {
            try {
                kafkaTemplate.send("payment-responses", message.getPayload());
                
                message.setProcessed(true);
                outboxMessageRepository.save(message);
            } catch (Exception e) {
            }
        }
    }
} 