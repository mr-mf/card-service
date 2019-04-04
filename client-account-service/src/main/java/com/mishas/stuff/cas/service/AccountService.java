package com.mishas.stuff.cas.service;

import com.mishas.stuff.cas.repository.DataSource;
import com.mishas.stuff.cas.repository.dao.AccountRepository;
import com.mishas.stuff.cas.repository.dao.TransactionStatusRepository;
import com.mishas.stuff.cas.repository.model.Account;
import com.mishas.stuff.cas.repository.model.TransactionStatus;
import com.mishas.stuff.cas.utils.Status;
import com.mishas.stuff.cas.web.dto.AccountDto;
import com.mishas.stuff.cas.web.dto.TransactionDto;
import com.mishas.stuff.cas.web.dto.TransactionStatusDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class AccountService implements IAccountService {

    private AccountRepository accountRepository;
    private TransactionStatusRepository transactionStatusRepository;

    public AccountService(AccountRepository accountRepository, TransactionStatusRepository transactionStatusRepository) {
        this.accountRepository = accountRepository;
        this.transactionStatusRepository = transactionStatusRepository;
    }

    @Override
    public Long createAccount(AccountDto accountDto) {
        final Account account = new Account(accountDto);
        return accountRepository.createAccount(account);
    }

    @Override
    public AccountDto getAccount(Long id) {
        Account account = accountRepository.getAccount(id);
        return new AccountDto(account);
    }

    @Override
    public TransactionStatusDto updateAccount(TransactionDto transactionDto) {
        TransactionStatusDto transactionStatusDto = null;
        Connection connection = null;
        try {
            connection = DataSource.getConnection();
            Account account = accountRepository.getAccountForUpdate(transactionDto.getClientCardNumber(), connection);
            // get the account balance and the transaction amount and calculate the difference
            BigDecimal accountBalance = account.getBalance().setScale(2, RoundingMode.HALF_UP);
            BigDecimal transaction = transactionDto.getTransactionAmount().setScale(2, RoundingMode.HALF_UP);
            BigDecimal remainingAccountBalance = accountBalance.subtract(transaction).setScale(2, RoundingMode.HALF_UP);

            transactionStatusDto = new TransactionStatusDto(transactionDto.getCorrelationId(), LocalDateTime.now());
            if (remainingAccountBalance.compareTo(BigDecimal.ZERO) < 0) {
                // set transaction status to DECLINED
                transactionStatusDto.setTransactionStaus(Status.DECLINED);
            } else {
                // set transaction status to ACCEPTED
                transactionStatusDto.setTransactionStaus(Status.ACCEPTED);
                // update the balance for the account model
                account.setBalance(remainingAccountBalance);
                accountRepository.updateAccount(account, connection);
            }
            // update the transaction status table
            transactionStatusRepository.updateTransactionStatus(new TransactionStatus(transactionStatusDto), connection);
            connection.commit();

        } catch (SQLException se) {

            try {
                connection.rollback();
            } catch (SQLException | NullPointerException se2) {

            }

        } finally {
            try {if (connection != null) { connection.close(); } } catch (SQLException sq3) { }
        }
        return transactionStatusDto;
    }

}
