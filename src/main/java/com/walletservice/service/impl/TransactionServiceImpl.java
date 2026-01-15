package com.walletservice.service.impl;

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
import com.walletservice.service.TransactionService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link TransactionService} interface.
 * Provides core operations for transaction management,
 * including crediting, debiting and transfer.
 *
 * @author Chibuike Okeke
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    @Transactional
    @Override
    public TransactionResponse createOrDebit(TransactionRequest transactionRequest) {

        UUID walletId =  transactionRequest.getWalletId();
        Integer amount = transactionRequest.getAmount();
        String transactionType = transactionRequest.getType();
        String key = transactionRequest.getIdempotencyKey();

        if (transactionRepository.existsByIdempotencyKey(key)){
            // A transaction already exist with that key
            throw new ServiceException("A transaction with the idempotency key already exists!");
        }

        TransactionType selectedTransactionType;

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if(transactionType.equals(TransactionType.DEBIT.name())) {

            if(wallet.getBalance() < amount){
                throw new ServiceException("Transaction failed with insufficient fund!");
            }

            selectedTransactionType = TransactionType.DEBIT;
            wallet.debit(amount);
        }else if(transactionType.equals(TransactionType.CREDIT.name())) {
            selectedTransactionType = TransactionType.CREDIT;
            wallet.credit(amount);
        }else{
            // invalid transaction type provided
            throw new ServiceException("Invalid transaction type: "+ transactionRequest.getType());
        }

        Transaction savedTransaction = transactionRepository.save(new Transaction(walletId, amount, selectedTransactionType, key));
        return mapToTransactionResponse(savedTransaction, transactionRequest);
    }

    @Transactional
    @Override
    public TransferResponse transfer(TransferRequest transferRequest) {

        UUID fromWalletId = transferRequest.getSenderWalletId();
        UUID toWalletId = transferRequest.getReceiverWalletId();
        Integer amount = transferRequest.getAmount();
        String key = transferRequest.getIdempotencyKey();

        if (transactionRepository.existsByIdempotencyKey(key)){
            // A transaction already exist with that key
            throw new ServiceException("A transaction with the idempotency key already exists!");
        }

        Wallet sender = walletRepository.findById(fromWalletId)
                .orElseThrow(() -> new NotFoundException("Sender wallet not found!"));
        Wallet receiver = walletRepository.findById(toWalletId)
                .orElseThrow(() -> new NotFoundException("Receiver wallet not found!"));

        if(sender.getBalance() < amount) {
            throw new ServiceException("Transaction failed with insufficient fund from sender!");
        }
        sender.debit(amount);
        receiver.credit(amount);

        Transaction outGoingTransaction = transactionRepository.save(new Transaction(fromWalletId, amount, TransactionType.TRANSFER_OUT, key));
        transactionRepository.save(new Transaction(toWalletId, amount, TransactionType.TRANSFER_IN, key));
        return mapToTransferResponse(outGoingTransaction, transferRequest);
    }

    @Override
    public List<TransactionDto> findAll() {
        return transactionRepository.findAll()
                .stream()
                .map(this::mapToTransactionDto)
                .toList();
    }

    private TransactionDto mapToTransactionDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .walletId(transaction.getWalletId())
                .amount(transaction.getAmount())
                .type(transaction.getType().name())
                .idempotencyKey(transaction.getIdempotencyKey())
                .build();
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction, TransactionRequest request) {

        return TransactionResponse.builder()
                .transactionId(transaction.getId())
                .walletId(transaction.getWalletId())
                .amount(transaction.getAmount())
                .type(request.getType())
                .idempotencyKey(transaction.getIdempotencyKey())
                .build();
    }

    private TransferResponse mapToTransferResponse(Transaction transaction, TransferRequest request) {
        return TransferResponse.builder()
                .transactionId(transaction.getId())
                .senderWalletId(request.getSenderWalletId())
                .receiverWalletId(request.getReceiverWalletId())
                .amount(transaction.getAmount())
                .idempotencyKey(transaction.getIdempotencyKey())
                .build();
    }
}
