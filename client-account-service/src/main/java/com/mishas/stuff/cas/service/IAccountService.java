package com.mishas.stuff.cas.service;

import com.mishas.stuff.cas.web.dto.AccountDto;
import com.mishas.stuff.cas.web.dto.TransactionDto;
import com.mishas.stuff.cas.web.dto.TransactionStatusDto;

public interface IAccountService {

    public Long createAccount(AccountDto accountDto);

    public AccountDto getAccount(Long id);

    public TransactionStatusDto updateAccount(TransactionDto transactionDto);
}
