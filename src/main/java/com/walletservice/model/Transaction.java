package com.walletservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transactions")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID walletId;
    private Integer amount;
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private String idempotencyKey;

    public Transaction(UUID walletId, int amount, TransactionType type, String idempotencyKey) {
        this.walletId = walletId;
        this.amount = amount;
        this.type = type;
        this.idempotencyKey = idempotencyKey;
    }
}
