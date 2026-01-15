package com.walletservice.repository;

import com.walletservice.model.Transaction;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Transaction} entities.
 * <p>
 * Provides CRUD operations and custom queries for transactions persistence
 *
 * @author Chibuike Okeke
 * @version 1.0
 * @since 1.0
 * @see JpaRepository
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    /**
     * Checks if a transaction already exists with the given idempotency key.
     *
     * @param idempotencyKey idempotency key
     * @return a boolean true/false
     */
    boolean existsByIdempotencyKey(String idempotencyKey);
}
