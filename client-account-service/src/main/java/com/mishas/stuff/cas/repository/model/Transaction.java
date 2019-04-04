package com.mishas.stuff.cas.repository.model;

import com.mishas.stuff.cas.web.dto.TransactionDto;

import java.math.BigDecimal;

public class Transaction implements IModel {

    private String correlationId;
    private BigDecimal transactionAmount;
    private String transactionCurrency;
    private String clientCardNumber;

    public Transaction() {
        super();
    }

    public Transaction(TransactionDto transactionDto) {
        this.correlationId = transactionDto.getCorrelationId();
        this.transactionAmount = transactionDto.getTransactionAmount();
        this.transactionCurrency = transactionDto.getTransactionCurrency();
        this.clientCardNumber = transactionDto.getClientCardNumber();
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public String getTransactionCurrency() {
        return transactionCurrency;
    }

    public String getClientCardNumber() {
        return clientCardNumber;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "correlationId='" + correlationId + '\'' +
                ", transactionAmount=" + transactionAmount +
                ", transactionCurrency='" + transactionCurrency + '\'' +
                ", clientCardNumber='" + clientCardNumber + '\'' +
                '}';
    }
}

