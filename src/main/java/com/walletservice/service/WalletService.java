package com.walletservice.service;

import com.walletservice.dto.WalletDto;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing wallets
 * This service provides business logic for creating and retrieving wallets.
 *
 * @author ChibuikeOkeke
 * @version 1.0
 * @since 1.0
 */
public interface WalletService {
    WalletDto createWallet();
    WalletDto findById(UUID id);
    List<WalletDto> findAll();
}
