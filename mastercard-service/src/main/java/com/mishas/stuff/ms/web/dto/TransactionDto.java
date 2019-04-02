package com.mishas.stuff.ms.web.dto;


import com.mishas.stuff.ms.repository.model.Transaction;

import java.math.BigDecimal;

public class TransactionDto implements IDto {

    private Long id;
    private String mastercardTransactionId;
    private String correlationId;
    private BigDecimal transactionAmount;
    private String transactionCurrency;
    private String firstname;
    private String surname;
    private String clientCardNumber;

    public TransactionDto () {
        super();
    }

    public TransactionDto(
            String mastercardTransactionId,
            BigDecimal transaction_amount,
            String transactionCurrency,
            String firstname,
            String surname,
            String clientCardNumber) {
        this.mastercardTransactionId = mastercardTransactionId;
        this.transactionAmount = transactionAmount;
        this.transactionCurrency = transactionCurrency;
        this.firstname = firstname;
        this.surname = surname;
        this.clientCardNumber = clientCardNumber;
    }

    public TransactionDto(Transaction transaction) {
        this.id = transaction.getId();
        this.mastercardTransactionId = transaction.getMastercardTransactionId();
        this.correlationId = transaction.getCorrelationId();
        this.transactionAmount = transaction.getTransactionAmount();
        this.transactionCurrency = transaction.getTransactionCurrency();
        this.firstname = transaction.getFirstname();
        this.surname = transaction.getSurname();
        this.clientCardNumber = transaction.getClientCardNumber();
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

    public String getTransactionCurrency() { return transactionCurrency;
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
        return "TransactionDto{" +
                ", mastercardTransactionId='" + mastercardTransactionId + '\'' +
                ", transaction_amount=" + transactionAmount +
                ", transactionCurrency='" + transactionCurrency + '\'' +
                ", firstname='" + firstname + '\'' +
                ", surname='" + surname + '\'' +
                ", clientCardNumber=" + clientCardNumber +
                '}';
    }
}
