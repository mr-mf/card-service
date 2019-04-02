package com.mishas.stuff.cas.web.dto;

import com.mishas.stuff.cas.repository.model.Account;

import java.math.BigDecimal;

public class AccountDto {

    private Long clientId;
    private BigDecimal balance;
    private String currency;
    private String cardNumber;

    public AccountDto() {
        super();
    }

    public AccountDto( BigDecimal balance, String currency, String cardNumber) {
        this.balance = balance;
        this.currency = currency;
        this.cardNumber = cardNumber;
    }

    public AccountDto(Account account) {
        this.clientId = account.getClientId();
        this.balance = account.getBalance();
        this.currency = account.getCurrency();
        this.cardNumber = account.getCardNumber();
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
        return "AccountDto{" +
                "clientId=" + clientId +
                ", balance=" + balance +
                ", currency='" + currency + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                '}';
    }
}