package com.payments.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payments.model.InboxEvent;
import com.payments.repository.InboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InboxListener {
    private final InboxEventRepository inboxEventRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payment-requests", groupId = "payments-service-group")
    @Transactional
    public void handlePaymentRequest(String message, String messageId) {

        if (inboxEventRepository.existsByMessageId(messageId)) {
            return;
        }

        InboxEvent inboxEvent = new InboxEvent();
        inboxEvent.setMessageId(messageId);
        inboxEvent.setEventType("PAYMENT_REQUEST");
        inboxEvent.setPayload(message);
        inboxEventRepository.save(inboxEvent);
    }
} 