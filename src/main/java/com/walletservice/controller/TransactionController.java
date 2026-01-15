package com.walletservice.controller;

import com.walletservice.dto.TransactionDto;
import com.walletservice.dto.TransactionRequest;
import com.walletservice.dto.TransactionResponse;
import com.walletservice.dto.TransferRequest;
import com.walletservice.dto.TransferResponse;
import com.walletservice.exception.ErrorResponse;
import com.walletservice.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing transactions.
 * <p>
 * Provides endpoints for creating / getting transactions and transfers.
 *
 * @author Chibuike Okeke
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Create a transaction for DEBIT/CREDIT.
     *
     * @param request the transaction creation request
     * @return TransactionResponse if successful!
     * @throws com.walletservice.exception.NotFoundException if the given wallet is not found!
     */
    @Operation(
            summary = "Create debit/credit transaction.",
            description = "Creates a debit/credit Transaction."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Transaction successful!",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponse.class)
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
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.createOrDebit(request));
    }

    /**
     * Create a transaction for transfers between wallets
     *
     * @param request the transfer request
     * @return transfer response if successful!
     * @throws com.walletservice.exception.NotFoundException if either of the wallets (to/from) not found
     */
    @Operation(
            summary = "Create a transfer transaction.",
            description = "Creates a Transfer Transaction."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Transfer successful!",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Wallet not found for either sender/receiver",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.transfer(request));
    }

    /**
     * Retrieve all transactions
     *
     * @return list of transactions
     */
    @Operation(
            summary = "Get all transactions",
            description = "Retrieves all transactions"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Transactions successfully retrieved!",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = TransactionDto.class)
                    )
            )
    )
    @GetMapping
    public ResponseEntity<List<TransactionDto>> getTransactions() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(transactionService.findAll());
    }
}
