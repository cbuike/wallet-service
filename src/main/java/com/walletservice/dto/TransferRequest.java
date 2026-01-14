package com.walletservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class TransferRequest {
    private UUID senderWalletId;
    private UUID receiverWalletId;

    @Schema(description = "amount", example = "100")
    private Integer amount;

    @Schema(description = "idempotencyKey", example = "ID_kEY_0987")
    private String idempotencyKey;
}
