package com.walletservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.walletservice.dto.WalletDto;
import com.walletservice.integration.AbstractionControllerBaseTest;
import com.walletservice.service.WalletService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class WalletControllerTest extends AbstractionControllerBaseTest {

    private  static final String BASE_URL="/wallets";

    @MockitoBean
    private WalletService walletService;


    private final UUID walletId = UUID.randomUUID();
    private WalletDto walletDto;

    @BeforeEach
    void setUp() {

        // Arrange
        walletDto = WalletDto.builder()
                .id(walletId)
                .balance(0)
                .build();
    }

    @Test
    @DisplayName("POST" + BASE_URL + " - Create Wallet")
    void testCreateWallet_thenReturnSuccess() throws Exception {

        // Act
        when(walletService.createWallet()).thenReturn(walletDto);

        // Assert
        performPost(BASE_URL, null)
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("GET" + BASE_URL + " - Get Wallet")
    void testGetWallet_thenReturnSuccess() throws Exception {

        // Act
        when(walletService.findById(any(UUID.class)))
                .thenReturn(walletDto);

        // Assert
        performGet(BASE_URL + "/" + walletId)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET" + BASE_URL + " - Get all Wallets")
    void testGetWallets_thenReturnSuccess() throws Exception {

        // Arrange
        List<WalletDto> createdWallets = new ArrayList<>();
        createdWallets.add(walletDto);

        // Act
        when(walletService.findAll())
                .thenReturn(createdWallets);

        // Assert
        performGet(BASE_URL)
                .andExpect(status().isOk());
    }
}
