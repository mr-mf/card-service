package com.mishas.stuff.ms.service;

import com.mishas.stuff.ms.web.dto.IDto;
import com.mishas.stuff.ms.web.dto.TransactionDto;
import com.mishas.stuff.ms.web.dto.TransactionStatusDto;

import java.util.List;

public interface IRecordKeepingService {
    public TransactionStatusDto createTransaction(TransactionDto transactionDto);

    public List<IDto> getTransaction(String correlationId);

    public void updateTransaction(String correlationId, TransactionStatusDto transactionStatusDto);
}
