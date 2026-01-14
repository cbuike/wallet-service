package com.walletservice.model;

import com.walletservice.exception.ServiceException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "wallets")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Integer balance;

    public void credit(int amount) {
        this.balance += amount;
    }

    public void debit(int amount) {
        if (balance < amount) {
            throw new ServiceException("Insufficient balance");
        }
        this.balance -= amount;
    }
}
