package com.walletservice.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class WalletTest {

    private final UUID WALLET_ID = UUID.randomUUID();

    @Test
    void testWalletBuilder() {

        // Arrange
        Wallet wallet = Wallet.builder()
                .id(WALLET_ID)
                .balance(50)
                .build();

        // Assert
        assertEquals(WALLET_ID, wallet.getId());
        assertEquals(50, wallet.getBalance());
    }

    @Test
    void testNoArgsConstructor() {
        // Arrange
        Wallet wallet = new Wallet();

        // Assert
        assertNotNull( wallet);
    }

    @Test
    void testALlArgsConstructor() {
        // Arrange
        Wallet wallet = new Wallet(WALLET_ID, 20);

        // Assert
        assertEquals(WALLET_ID, wallet.getId());
        assertEquals(20, wallet.getBalance());
    }
}
