package com.mishas.stuff.cas.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDto implements IDto {

    private String correlationId;
    private BigDecimal transactionAmount;
    private String transactionCurrency;
    private String clientCardNumber;

    public TransactionDto() {}

    public TransactionDto(String correlationId, BigDecimal transactionAmount, String transactionCurrency, String clientCardNumber) {
        this.correlationId = correlationId;
        this.transactionAmount = transactionAmount;
        this.transactionCurrency = transactionCurrency;
        this.clientCardNumber = clientCardNumber;
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
        return "TransactionDto{" +
                "correlationId='" + correlationId + '\'' +
                ", transactionAmount=" + transactionAmount +
                ", transactionCurrency='" + transactionCurrency + '\'' +
                ", clientCardNumber='" + clientCardNumber + '\'' +
                '}';
    }
}
