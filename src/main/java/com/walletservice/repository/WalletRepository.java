package com.walletservice.repository;

import com.walletservice.model.Wallet;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Wallet} entities.
 * <p>
 * Provides CRUD operations and custom queries for wallet persistence
 *
 * @author Chibuike Okeke
 * @version 1.0
 * @since 1.0
 * @see JpaRepository
 */
@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> { }
