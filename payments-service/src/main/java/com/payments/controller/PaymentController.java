package com.payments.controller;

import com.payments.model.Account;
import com.payments.repository.AccountRepository;
import com.payments.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Account Controller", description = "Account management APIs")
public class PaymentController {
    private final AccountRepository accountRepository;
    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Create a new account")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        account.setBalance(BigDecimal.ZERO);
        return ResponseEntity.ok(accountRepository.save(account));
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get account balance by ID")
    public ResponseEntity<Account> getAccount(@PathVariable Long accountId) {
        return accountRepository.findById(accountId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{accountId}/deposit")
    @Operation(summary = "Deposit money into an account")
    public ResponseEntity<Account> depositMoney(@PathVariable Long accountId, @RequestParam BigDecimal amount) {
        try {
            Account updatedAccount = paymentService.deposit(accountId, amount);
            return ResponseEntity.ok(updatedAccount);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{accountId}/withdraw")
    @Operation(summary = "Withdraw money from an account")
    public ResponseEntity<Account> withdrawMoney(@PathVariable Long accountId, @RequestParam BigDecimal amount) {
        try {
            Account updatedAccount = paymentService.withdraw(accountId, amount);
            return ResponseEntity.ok(updatedAccount);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 