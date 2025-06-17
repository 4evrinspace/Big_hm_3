package com.payments.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payments.model.Account;
import com.payments.model.OutboxMessage;
import com.payments.repository.AccountRepository;
import com.payments.repository.OutboxMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final AccountRepository accountRepository;
    private final OutboxMessageRepository outboxMessageRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public void processPayment(String paymentRequest) {
        try {
            PaymentRequest request = objectMapper.readValue(paymentRequest, PaymentRequest.class);
            
            Account account = accountRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
            
            if (account.getLastProcessedPaymentId() != null && 
                account.getLastProcessedPaymentId().equals(request.getOrderId())) {
                return;
            }
            if (account.getBalance().compareTo(BigDecimal.valueOf(request.getAmount())) >= 0) {
                account.setBalance(account.getBalance().subtract(BigDecimal.valueOf(request.getAmount())));
                account.setLastProcessedPaymentId(request.getOrderId());
                accountRepository.save(account);
                
                OutboxMessage outboxMessage = new OutboxMessage();
                outboxMessage.setEventType("PAYMENT_RESPONSE");
                outboxMessage.setPayload(String.format(
                    "{\"orderId\":%d,\"status\":\"FINISHED\"}", 
                    request.getOrderId()));
                outboxMessageRepository.save(outboxMessage);
            } else {
                OutboxMessage outboxMessage = new OutboxMessage();
                outboxMessage.setEventType("PAYMENT_RESPONSE");
                outboxMessage.setPayload(String.format(
                    "{\"orderId\":%d,\"status\":\"FAILED\"}", 
                    request.getOrderId()));
                outboxMessageRepository.save(outboxMessage);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to process payment", e);
        }
    }
    
    @Transactional
    public Account deposit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setBalance(account.getBalance().add(amount));
        return accountRepository.save(account);
    }

    @Transactional
    public Account withdraw(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }
        account.setBalance(account.getBalance().subtract(amount));
        return accountRepository.save(account);
    }

    private static class PaymentRequest {
        private Long orderId;
        private Long userId;
        private Double amount;
        
        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
    }
} 