package com.walletservice.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.walletservice.dto.TransactionDto;
import com.walletservice.dto.TransactionRequest;
import com.walletservice.dto.TransactionResponse;
import com.walletservice.dto.TransferRequest;
import com.walletservice.dto.TransferResponse;
import com.walletservice.integration.AbstractionControllerBaseTest;
import com.walletservice.model.Transaction;
import com.walletservice.model.TransactionType;
import com.walletservice.service.TransactionService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class TransactionControllerTest extends AbstractionControllerBaseTest {

    private  static final String BASE_URL = "/transactions";

    @MockitoBean
    private TransactionService transactionService;
    private final UUID transactionId = UUID.randomUUID();
    private final UUID senderWalletId = UUID.randomUUID();
    private final UUID receiverWalletId = UUID.randomUUID();
    private final String idempotencyKey = "IDKEY-09876567";

    private TransactionResponse.TransactionResponseBuilder walletTransactionBuilder = TransactionResponse.builder();

    @BeforeEach
    void setUp() {
        walletTransactionBuilder =  TransactionResponse.builder()
                .transactionId(transactionId)
                .walletId(senderWalletId)
                .amount(50)
                .type(TransactionType.CREDIT.name())
                .idempotencyKey(idempotencyKey);
    }

    @Test
    @DisplayName("POST - " + BASE_URL + " - Create Credit Wallet Transaction")
    void testCreateWalletCreditTransaction_thenReturnSuccess() throws Exception {

        TransactionRequest request = TransactionRequest.builder()
                .walletId(senderWalletId)
                .amount(100)
                .type(TransactionType.CREDIT.name())
                .idempotencyKey(idempotencyKey)
                .build();

        // Act
        when(transactionService.createOrDebit(request))
                .thenReturn(walletTransactionBuilder.build());

        // Assert
        performPost(BASE_URL, request)
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST - " + BASE_URL + " - Create Debit Wallet Transaction")
    void testCreateWalletDebitTransaction_thenReturnSuccess() throws Exception {

        TransactionRequest request = TransactionRequest.builder()
                .walletId(senderWalletId)
                .amount(10)
                .type(TransactionType.DEBIT.name())
                .idempotencyKey(idempotencyKey)
                .build();

        TransactionResponse transaction = walletTransactionBuilder
                                    .type(TransactionType.DEBIT.name())
                                            .amount(20).build();

        // Act
        when(transactionService.createOrDebit(request))
                .thenReturn(transaction);

        // Assert
        performPost(BASE_URL, request)
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST - " + BASE_URL + "/transfer - Create transfer Transaction")
    void testCreateTransferTransaction_thenReturnSuccess() throws Exception {

        TransferRequest request = TransferRequest.builder()
                .senderWalletId(senderWalletId)
                .receiverWalletId(receiverWalletId)
                .amount(50)
                .idempotencyKey(idempotencyKey)
                .build();

        TransferResponse response = TransferResponse.builder()
                .transactionId(transactionId)
                .senderWalletId(senderWalletId)
                .receiverWalletId(receiverWalletId)
                .amount(20)
                .idempotencyKey(idempotencyKey)
                .build();

        // Act
        when(transactionService.transfer(request))
                .thenReturn(response);

        // Assert
        performPost(BASE_URL + "/transfer", request)
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("GET - " + BASE_URL + " - Get All Transactions")
    void getAllTransactions_thenReturnSuccess() throws Exception {

        // Arrange
        Transaction transaction = Transaction.builder()
                .id(transactionId)
                .walletId(senderWalletId)
                .amount(50)
                .type(TransactionType.CREDIT)
                .idempotencyKey(idempotencyKey)
                .build();

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        List<TransactionDto> dbTransactions = transactionService.findAll();

        // Act
        when(transactionService.findAll())
                .thenReturn(dbTransactions);

        // Assert
        performGet(BASE_URL)
                .andExpect(status().isOk());
    }
}