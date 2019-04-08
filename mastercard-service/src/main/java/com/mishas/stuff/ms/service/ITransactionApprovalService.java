package com.mishas.stuff.ms.service;

import com.mishas.stuff.ms.web.dto.TransactionDto;
import com.mishas.stuff.ms.web.dto.TransactionStatusDto;

public interface ITransactionApprovalService {

    public TransactionStatusDto sendTransactionForApproval(TransactionDto transactionDto);
}
