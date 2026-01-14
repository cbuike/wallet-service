package com.walletservice.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionDto {

    private UUID id;
    private UUID walletId;
    private Integer amount;
    private String type;
    private String idempotencyKey;
}
