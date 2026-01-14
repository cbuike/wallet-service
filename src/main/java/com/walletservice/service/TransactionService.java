package com.walletservice.service;

import com.walletservice.dto.TransactionDto;
import com.walletservice.dto.TransactionRequest;
import com.walletservice.dto.TransactionResponse;
import com.walletservice.dto.TransferRequest;
import com.walletservice.dto.TransferResponse;
import java.util.List;

/**
 * Service interface for managing Transactions
 * This service provides business logic for making transactions
 *
 * @author ChibuikeOkeke
 * @version 1.0
 * @since 1.0
 */
public interface TransactionService {
    TransactionResponse createOrDebit(TransactionRequest request);
    TransferResponse transfer(TransferRequest request);
    List<TransactionDto> findAll();
}
