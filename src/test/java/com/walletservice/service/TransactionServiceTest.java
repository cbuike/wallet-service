package com.walletservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.walletservice.dto.TransactionDto;
import com.walletservice.dto.TransactionRequest;
import com.walletservice.dto.TransactionResponse;
import com.walletservice.dto.TransferRequest;
import com.walletservice.dto.TransferResponse;
import com.walletservice.exception.NotFoundException;
import com.walletservice.exception.ServiceException;
import com.walletservice.model.Transaction;
import com.walletservice.model.TransactionType;
import com.walletservice.model.Wallet;
import com.walletservice.repository.TransactionRepository;
import com.walletservice.repository.WalletRepository;
import com.walletservice.service.impl.TransactionServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private UUID transactionID = UUID.randomUUID();
    private UUID senderWalletId = UUID.randomUUID();
    private UUID receiverWalletId = UUID.randomUUID();

    private Transaction.TransactionBuilder transactionBuilder = Transaction.builder();
    private Wallet senderWallet;
    private Wallet receiverWallet;
    private final String idempotencyKey = "IDK-KEY-0023";


    @BeforeEach
    void setUp() {
        senderWallet = new Wallet(senderWalletId, 50);
        receiverWallet = new Wallet(receiverWalletId, 70);

        transactionBuilder
                .id(transactionID)
                .walletId(senderWalletId)
                .amount(30)
                .idempotencyKey(idempotencyKey);
    }

    @Test
    @DisplayName("Test Credit Wallet transaction")
    void testCreditWallet_thenReturnSuccess() {

        TransactionRequest request = TransactionRequest.builder()
                .walletId(senderWalletId)
                .amount(50)
                .type(TransactionType.CREDIT.name())
                .idempotencyKey(idempotencyKey)
                .build();

        Transaction transaction = transactionBuilder
                .id(transactionID)
                .walletId(senderWalletId)
                .amount(50)
                .type(TransactionType.CREDIT)
                .idempotencyKey(idempotencyKey)
                .build();


        when(transactionRepository.existsByIdempotencyKey(any(String.class)))
                .thenReturn(Boolean.FALSE);
        when(walletRepository.findById(senderWalletId)).thenReturn(Optional.of(senderWallet));
        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(transaction);

       TransactionResponse savedTransaction = transactionService.createOrDebit(request);

       assertNotNull(savedTransaction);
       assertEquals(savedTransaction.getWalletId(), request.getWalletId());
       assertEquals(savedTransaction.getAmount(), request.getAmount());
    }

    @Test
    @DisplayName("Test Debit Wallet transaction")
    void testDebitWallet_thenReturnSuccess() {

        TransactionRequest request = TransactionRequest.builder()
                .walletId(senderWalletId)
                .amount(50)
                .type(TransactionType.DEBIT.name())
                .idempotencyKey(idempotencyKey)
                .build();

        Transaction transaction = transactionBuilder
                .id(transactionID)
                .walletId(senderWalletId)
                .amount(50)
                .type(TransactionType.DEBIT)
                .idempotencyKey(idempotencyKey)
                .build();


        when(transactionRepository.existsByIdempotencyKey(any(String.class)))
                .thenReturn(Boolean.FALSE);
        when(walletRepository.findById(senderWalletId)).thenReturn(Optional.of(senderWallet));
        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(transaction);

        TransactionResponse savedTransaction = transactionService.createOrDebit(request);

        assertNotNull(savedTransaction);
        assertEquals(savedTransaction.getWalletId(), request.getWalletId());
        assertEquals(savedTransaction.getAmount(), request.getAmount());
    }

    @Test
    @DisplayName("Test Debit Wallet transaction with amount greater than balance")
    void testDebitWalletWithAmountGreaterThanBalance_thenThrowException() {

        TransactionRequest request = TransactionRequest.builder()
                .walletId(senderWalletId)
                .amount(70)
                .type(TransactionType.DEBIT.name())
                .idempotencyKey(idempotencyKey)
                .build();


        when(transactionRepository.existsByIdempotencyKey(any(String.class)))
                .thenReturn(Boolean.FALSE);
        when(walletRepository.findById(senderWalletId)).thenReturn(Optional.of(senderWallet));

        ServiceException thrown = assertThrows(
                ServiceException.class,
                () -> transactionService.createOrDebit(request)
        );

        // Assert
        assertEquals("Transaction failed with insufficient fund!", thrown.getMessage());
    }

    @Test
    @DisplayName("Test Credit Wallet with invalid transaction type")
    void testCreditWalletWithInvalidTransactionType_thenThrowException() {

        TransactionRequest request = TransactionRequest.builder()
                .walletId(senderWalletId)
                .amount(50)
                .type("INVALID-TYPE")
                .idempotencyKey(idempotencyKey)
                .build();

        when(transactionRepository.existsByIdempotencyKey(any(String.class)))
                .thenReturn(Boolean.FALSE);

        when(walletRepository.findById(senderWalletId))
                .thenReturn(Optional.of(senderWallet));

        ServiceException thrown = assertThrows(
                ServiceException.class,
                () -> transactionService.createOrDebit(request)
        );

        // Assert
        assertEquals("Invalid transaction type: INVALID-TYPE", thrown.getMessage());
    }

    @Test
    @DisplayName("Test Credit Wallet transaction with existing Idempotency key")
    void testCreditWalletWithExistingIdempotencyKey_thenThrowException() {

        TransactionRequest request = TransactionRequest.builder()
                .walletId(senderWalletId)
                .amount(50)
                .type(TransactionType.CREDIT.name())
                .idempotencyKey(idempotencyKey)
                .build();


        when(transactionRepository.existsByIdempotencyKey(any(String.class)))
                .thenReturn(Boolean.TRUE);

        ServiceException thrown = assertThrows(
                ServiceException.class,
                () -> transactionService.createOrDebit(request)
        );

        // Assert
        assertEquals("A transaction with the idempotency key already exists!", thrown.getMessage());
    }

    @Test
    @DisplayName("Test Create transfer - Success")
    void testCreateTransfer_thenReturnSuccess() {

        // Arrange
        TransferRequest request = TransferRequest.builder()
                .senderWalletId(senderWalletId)
                .receiverWalletId(receiverWalletId)
                .amount(50)
                .idempotencyKey(idempotencyKey)
                .build();

        Transaction transactionFrom = transactionBuilder
                .id(transactionID)
                .walletId(senderWalletId)
                .amount(50)
                .type(TransactionType.TRANSFER_OUT)
                .idempotencyKey(idempotencyKey)
                .build();

        Transaction transactionTo = transactionBuilder
                .id(transactionID)
                .walletId(senderWalletId)
                .amount(50)
                .type(TransactionType.TRANSFER_IN)
                .idempotencyKey(idempotencyKey)
                .build();

        // Act

        when(transactionRepository.existsByIdempotencyKey(any(String.class)))
                .thenReturn(Boolean.FALSE);

        when(walletRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(senderWallet), Optional.of(receiverWallet));

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(transactionFrom, transactionTo);

        TransferResponse response = transactionService.transfer(request);

        // Assert
        assertNotNull(response);
        assertEquals(response.getTransactionId(), transactionFrom.getId());
    }

    @Test
    @DisplayName("Test Create transfer with Invalid Sender Wallet ")
    void testCreateTransferWithInvalidSenderWallet_thenThrowException() {

        // Arrange
        TransferRequest request = TransferRequest.builder()
                .senderWalletId(senderWalletId)
                .receiverWalletId(receiverWalletId)
                .amount(50)
                .idempotencyKey(idempotencyKey)
                .build();

        // Act

        when(transactionRepository.existsByIdempotencyKey(any(String.class)))
                .thenReturn(Boolean.FALSE);

        when(walletRepository.findById(senderWalletId)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> transactionService.transfer(request)
        );

        // Assert
        assertEquals("Sender wallet not found!", thrown.getMessage());
    }

    @Test
    @DisplayName("Test Create transfer with Invalid Receiver Wallet ")
    void testCreateTransferWithInvalidReceiverWallet_thenThrowException() {

        // Arrange
        TransferRequest request = TransferRequest.builder()
                .senderWalletId(senderWalletId)
                .receiverWalletId(receiverWalletId)
                .amount(50)
                .idempotencyKey(idempotencyKey)
                .build();

        // Act

        when(transactionRepository.existsByIdempotencyKey(any(String.class)))
                .thenReturn(Boolean.FALSE);

        when(walletRepository.findById(senderWalletId))
                .thenReturn(Optional.of(senderWallet), Optional.empty());

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> transactionService.transfer(request)
        );

        // Assert
        assertEquals("Receiver wallet not found!", thrown.getMessage());
    }

    @Test
    @DisplayName("Test Create transfer with Existing Idempotency Key ")
    void testCreateTransferWithExistingIdempotencyKey_thenThrowException() {

        // Arrange
        TransferRequest request = TransferRequest.builder()
                .senderWalletId(senderWalletId)
                .receiverWalletId(receiverWalletId)
                .amount(50)
                .idempotencyKey(idempotencyKey)
                .build();

        // Act
        when(transactionRepository.existsByIdempotencyKey(any(String.class)))
                .thenReturn(Boolean.TRUE);

        ServiceException thrown = assertThrows(
                ServiceException.class,
                () -> transactionService.transfer(request)
        );

        // Assert
        assertEquals("A transaction with the idempotency key already exists!", thrown.getMessage());
    }

    @Test
    @DisplayName("Test Create transfer with Amount Exceeding Sender waller balance ")
    void testCreateTransferWithAmountExceedingSenderWalletBalance_thenThrowException() {

        // Arrange
        TransferRequest request = TransferRequest.builder()
                .senderWalletId(senderWalletId)
                .receiverWalletId(receiverWalletId)
                .amount(70)
                .idempotencyKey(idempotencyKey)
                .build();

        // Act
        when(transactionRepository.existsByIdempotencyKey(any(String.class)))
                .thenReturn(Boolean.FALSE);

        when(walletRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(senderWallet), Optional.of(receiverWallet));

        ServiceException thrown = assertThrows(
                ServiceException.class,
                () -> transactionService.transfer(request)
        );

        // Assert
        assertEquals("Transaction failed with insufficient fund from sender!", thrown.getMessage());
    }

    @Test
    @DisplayName("Get All Transactions")
    void testGetAllTransactions() {

        Transaction transaction = Transaction.builder()
                .id(transactionID)
                .walletId(senderWalletId)
                .amount(50)
                .type(TransactionType.CREDIT)
                .idempotencyKey(idempotencyKey)
                .build();

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        when(transactionRepository.findAll())
                .thenReturn(transactions);

        List<TransactionDto> dbTransactions = transactionService.findAll();

        assertTrue(!dbTransactions.isEmpty());
        assertTrue(transactions.contains(transaction));
    }
}
