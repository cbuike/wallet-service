package com.walletservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionRequest {
    private UUID walletId;

    @Schema(description = "amount", example = "100")
    private Integer amount;

    @Schema(description = "transaction type", example = "DEBIT or CREDIT")
    private String type;

    @Schema(description = "idempotencyKey", example = "ID_kEY_0922")
    private String idempotencyKey;
}
