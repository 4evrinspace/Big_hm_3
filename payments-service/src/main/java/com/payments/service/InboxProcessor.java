package com.payments.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payments.model.InboxEvent;
import com.payments.repository.InboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InboxProcessor {
    private final InboxEventRepository inboxEventRepository;
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 1000)
    @Transactional
    public void processInboxMessages() {
        List<InboxEvent> messages = inboxEventRepository.findByProcessedFalse();
        for (InboxEvent message : messages) {
            try {

                if ("PAYMENT_REQUEST".equals(message.getEventType())) {
                    paymentService.processPayment(message.getPayload());
                }
                

                message.setProcessed(true);
                inboxEventRepository.save(message);
            } catch (Exception e) {

            }
        }
    }
} 