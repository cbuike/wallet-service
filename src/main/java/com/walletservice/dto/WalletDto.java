package com.walletservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WalletDto {
    private UUID id;

    @Schema(description = "wallet balance", example = "0")
    private int balance;
}
