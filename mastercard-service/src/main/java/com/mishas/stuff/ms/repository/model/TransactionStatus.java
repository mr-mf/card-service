package com.mishas.stuff.ms.repository.model;

import com.mishas.stuff.ms.utils.Status;
import com.mishas.stuff.ms.web.dto.TransactionStatusDto;

import java.time.LocalDateTime;

public class TransactionStatus implements IModel {

    private String id;
    private LocalDateTime transactionTimestamp;
    private Status transactionStatus;

    public TransactionStatus(TransactionStatusDto transactionStatusDto) {
        this.id = transactionStatusDto.getId();
        this.transactionTimestamp = transactionStatusDto.getTransactionTimestamp();
        this.transactionStatus = transactionStatusDto.getTransactionStaus();
    }

    public TransactionStatus(String id, LocalDateTime transactionTimestamp, Status transactionStatus) {
        this.id = id;
        this.transactionTimestamp = transactionTimestamp;
        this.transactionStatus = transactionStatus;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getTransactionTimestamp() {
        return transactionTimestamp;
    }

    public Status getTransactionStatus() {
        return transactionStatus;
    }

    @Override
    public String toString() {
        return "TransactionStatus{" +
                "id='" + id + '\'' +
                ", transactionTimestamp=" + transactionTimestamp +
                ", transactionStatus=" + transactionStatus +
                '}';
    }
}
