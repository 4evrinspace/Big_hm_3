package com.example.ordersservice.repository;

import com.example.ordersservice.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
 
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findAllBySentFalse();
} 