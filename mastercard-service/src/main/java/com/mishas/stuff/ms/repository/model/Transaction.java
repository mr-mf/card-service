package com.mishas.stuff.ms.repository.model;

import com.mishas.stuff.ms.web.dto.TransactionDto;

import java.math.BigDecimal;

public class Transaction implements IModel {

    private Long id;
    private String mastercardTransactionId;
    private String correlationId;
    private BigDecimal transactionAmount;
    private String transactionCurrency;
    private String firstname;
    private String surname;
    private String clientCardNumber;

    public Transaction () {
        super();
    }

    public Transaction(
            Long id,
            String mastercardTransactionId,
            String correlationId,
            BigDecimal transactionAmount,
            String transactionCurrency,
            String firstname,
            String surname,
            String clientCardNumber) {
        this.id = id;
        this.mastercardTransactionId = mastercardTransactionId;
        this.correlationId = correlationId;
        this.transactionAmount = transactionAmount;
        this.transactionCurrency = transactionCurrency;
        this.firstname = firstname;
        this.surname = surname;
        this.clientCardNumber = clientCardNumber;
    }

    public Transaction(TransactionDto transactionDto) {
        this.mastercardTransactionId = transactionDto.getMastercardTransactionId();
        this.correlationId = transactionDto.getCorrelationId();
        this.transactionAmount = transactionDto.getTransactionAmount();
        this.transactionCurrency = transactionDto.getTransactionCurrency();
        this.firstname = transactionDto.getFirstname();
        this.surname = transactionDto.getSurname();
        this.clientCardNumber = transactionDto.getClientCardNumber();
    }

    public Long getId() {
        return id;
    }

    public String getMastercardTransactionId() {
        return mastercardTransactionId;
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

    public String getFirstname() {
        return firstname;
    }

    public String getSurname() {
        return surname;
    }

    public String getClientCardNumber() {
        return clientCardNumber;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", mastercardTransactionId='" + mastercardTransactionId + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", transactionAmount=" + transactionAmount +
                ", transactionCurrency='" + transactionCurrency + '\'' +
                ", firstname='" + firstname + '\'' +
                ", surname='" + surname + '\'' +
                ", clientCardNumber=" + clientCardNumber +
                '}';
    }
}
