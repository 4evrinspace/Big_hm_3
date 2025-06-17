package com.payments.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "balance")
    private BigDecimal balance;
    
    @Column(name = "last_processed_payment_id")
    private Long lastProcessedPaymentId;
    
    @Version
    private Long version;
} 