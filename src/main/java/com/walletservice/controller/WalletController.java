package com.walletservice.controller;

import com.walletservice.dto.WalletDto;
import com.walletservice.exception.ErrorResponse;
import com.walletservice.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing wallets.
 * <p>
 * Provides endpoints for creating, and retrieving wallets
 *
 * @author Chibuike Okeke
 * @version 1.0
 * @since 1.0
 */
@Tag(name = "Wallet", description = "Wallet management APIs")
@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    /**
     * Create a new wallet.
     *
     * @return the created wallet
     */
    @Operation(
            summary = "Create a wallet.",
            description = "Creates a new wallet."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Category successfully created",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = WalletDto.class)
            )
    )
    @PostMapping
    public ResponseEntity<WalletDto> createWallet() {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(walletService.createWallet());
    }

    /**
     * Retrieve a single wallet
     *
     * @param walletId the ID of the wallet
     * @return the Wallet
     * @throws com.walletservice.exception.NotFoundException if the wallet does not exist
     */
    @Operation(
            summary = "Get a wallet by Id",
            description = "Retrieves a wallet by its Id"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Wallet retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WalletDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Wallet not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<WalletDto> getWallet(@PathVariable("id") UUID walletId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(walletService.findById(walletId));
    }

    /**
     * Retrieve all wallets
     *
     * @return list of wallets
     */
    @Operation(
            summary = "Get all wallets",
            description = "Retrieves all wallets"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Wallets successfully retrieved!",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = WalletDto.class)
                    )
            )
    )
    @GetMapping()
    public ResponseEntity<List<WalletDto>> getWallets() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(walletService.findAll());
    }
}
