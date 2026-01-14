package com.walletservice.service.impl;

import com.walletservice.dto.WalletDto;
import com.walletservice.exception.NotFoundException;
import com.walletservice.model.Wallet;
import com.walletservice.repository.WalletRepository;
import com.walletservice.service.WalletService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Override
    public WalletDto createWallet() {
        Wallet wallet = Wallet.builder()
                .balance(0)
                .build();
        return mapToDto(walletRepository.save(wallet));
    }

    @Override
    public WalletDto findById(UUID id) {
        return walletRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new NotFoundException("Wallet not found with id: " + id));
    }

    @Override
    public List<WalletDto> findAll() {
        return walletRepository.findAll()
                .stream().map(this::mapToDto)
                .toList();
    }

    private WalletDto mapToDto(Wallet wallet) {
        return WalletDto.builder()
                .id(wallet.getId())
                .balance(wallet.getBalance())
                .build();
    }
}
