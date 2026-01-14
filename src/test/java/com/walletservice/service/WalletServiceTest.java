package com.walletservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.walletservice.dto.WalletDto;
import com.walletservice.exception.NotFoundException;
import com.walletservice.model.Wallet;
import com.walletservice.repository.WalletRepository;
import com.walletservice.service.impl.WalletServiceImpl;
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
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    private final WalletDto.WalletDtoBuilder walletDtoBuilder = WalletDto.builder();
    private final Wallet.WalletBuilder walletBuilder = Wallet.builder();
    private final UUID WALLET_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        walletDtoBuilder.id(WALLET_ID).balance(0);
        walletBuilder.id(WALLET_ID).balance(0);
    }

    @Test
    @DisplayName("Test Create Wallet")
    void testCreateWallet_thenReturnWallet() {

        // Arrange
        Wallet wallet = walletBuilder.build();

        // act
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        WalletDto createdWallet = walletService.createWallet();

        // Assert
        assertNotNull(createdWallet);
        assertEquals(createdWallet.getId(), wallet.getId());
        assertEquals(createdWallet.getBalance(), wallet.getBalance());
    }

    @Test
    @DisplayName("Test Get Existing Wallet")
    void testGetExistingWallet_thenReturnWallet() {

        // Arrange
        Wallet wallet = walletBuilder.build();

        // act
        when(walletRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(wallet));

        WalletDto dbWallet = walletService.findById(WALLET_ID);

        // Assert
        assertNotNull(dbWallet);
        assertEquals(dbWallet.getId(), wallet.getId());
    }

    @Test
    @DisplayName("Test Get Non Existing Wallet")
    void testGetNonExistingWallet_thenReturnWallet() {

        // Arrange

        // act
        when(walletRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> walletService.findById(WALLET_ID)
        );

        // Assert
        assertEquals("Wallet not found with id: "+WALLET_ID, thrown.getMessage());
        verify(walletRepository).findById(WALLET_ID);
    }

    @Test
    @DisplayName("Get Wallets")
    void testGetWallets_thenReturnAllWallets() {

        // Arrange
        Wallet wallet = walletBuilder.build();

        List<Wallet> wallets = new ArrayList<>();
        wallets.add(wallet);

        // act
        when(walletRepository.findAll())
                .thenReturn(wallets);

        List<WalletDto> createdWallets = walletService.findAll();

        // Assert
        assertEquals(createdWallets.size(), wallets.size());
    }
}
