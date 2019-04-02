package com.mishas.stuff.cas.repository.model;

import com.mishas.stuff.cas.web.dto.AccountDto;

import java.math.BigDecimal;

public class Account implements IModel {

    private Long clientId;
    private BigDecimal balance;
    private String currency;
    private String cardNumber;

    public Account() {
        super();
    }

    public Account(Long clientId, BigDecimal balance, String currency, String cardNumber) {
        this.clientId = clientId;
        this.balance = balance;
        this.currency = currency;
        this.cardNumber = cardNumber;
    }

    public Account(AccountDto accountDto) {
        this.balance = accountDto.getBalance();
        this.currency = accountDto.getCurrency();
        this.cardNumber = accountDto.getCardNumber();
    }

    public Long getClientId() {
        return clientId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    @Override
    public String toString() {
        return "Account{" +
                "clientId=" + clientId +
                ", balance=" + balance +
                ", currency='" + currency + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                '}';
    }
}
