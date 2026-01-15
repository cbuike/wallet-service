package com.walletservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class TransactionDto {

    private UUID id;
    private UUID walletId;

    @Schema(description = "amount", example = "100")
    private Integer amount;

    @Schema(description = "type", example = "CREDIT")
    private String type;

    @Schema(description = "idempotencyKey", example = "ID_KEY_802")
    private String idempotencyKey;
}
