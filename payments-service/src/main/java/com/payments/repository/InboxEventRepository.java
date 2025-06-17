package com.payments.repository;

import com.payments.model.InboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InboxEventRepository extends JpaRepository<InboxEvent, Long> {
    List<InboxEvent> findByProcessedFalse();
    boolean existsByMessageId(String messageId);
} 