package com.mishas.stuff.cas.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.mishas.stuff.cas.repository.model.TransactionStatus;
import com.mishas.stuff.cas.utils.Status;
import jdk.jfr.Timestamp;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

public class TransactionStatusDto implements IDto {

    private String id;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSS")
    private LocalDateTime transactionTimestamp;
    private Status transactionStatus;

    public TransactionStatusDto() {
        super();
    }

    public TransactionStatusDto(TransactionStatus transactionStatus) {
        this.id = transactionStatus.getId();
        this.transactionTimestamp = transactionStatus.getTransactionTimestamp();
        this.transactionStatus = transactionStatus.getTransactionStatus();
    }

    public TransactionStatusDto(String id, LocalDateTime transactionTimestamp) {
        this.id = id;
        this.transactionTimestamp = transactionTimestamp;
        this.transactionStatus = Status.PENDING;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getTransactionTimestamp() {
        return transactionTimestamp;
    }

    public Status getTransactionStaus() {
        return transactionStatus;
    }

    public void setTransactionStaus(Status transactionStaus) {
        this.transactionStatus = transactionStaus;
    }

    @Override
    public String toString() {
        return "TransactionStatusDto{" +
                "id='" + id + '\'' +
                ", transactionTimestamp=" + transactionTimestamp +
                ", transactionStatus=" + transactionStatus +
                '}';
    }
}
