package com.mishas.stuff.cas.service;

import com.mishas.stuff.cas.repository.DataSource;
import com.mishas.stuff.cas.repository.dao.AccountRepository;
import com.mishas.stuff.cas.repository.dao.TransactionStatusRepository;
import com.mishas.stuff.cas.repository.model.Account;
import com.mishas.stuff.cas.repository.model.TransactionStatus;
import com.mishas.stuff.cas.utils.Status;
import com.mishas.stuff.cas.utils.exceptions.DatabaseException;
import com.mishas.stuff.cas.web.dto.AccountDto;
import com.mishas.stuff.cas.web.dto.TransactionDto;
import com.mishas.stuff.cas.web.dto.TransactionStatusDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import org.apache.log4j.Logger;

public class AccountService implements IAccountService {

    private static final Logger logger = Logger.getLogger(AccountService.class);

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
        TransactionStatus transactionStatus;
        Connection connection = null;
        try {
            connection = DataSource.getConnection();
            Account account = accountRepository.getAccountForUpdate(transactionDto.getClientCardNumber(), connection);
            // get the account balance and the transaction amount and calculate the difference
            BigDecimal accountBalance = account.getBalance().setScale(2, RoundingMode.HALF_UP);
            BigDecimal transaction = transactionDto.getTransactionAmount().setScale(2, RoundingMode.HALF_UP);
            BigDecimal remainingAccountBalance;
            transactionStatus = new TransactionStatus(transactionDto.getCorrelationId(), LocalDateTime.now(), Status.PENDING);
            // update the transaction status table
            try {
                transactionStatusRepository.createTransactionStatus(transactionStatus, connection);
            } catch (SQLException seIgnore) {
                // ignore
                connection.rollback();
            }
            transactionStatus = transactionStatusRepository.getTransactionStatusForUpdate(transactionStatus.getId(), connection);
            if (!transactionStatus.getTransactionStatus().toString().equals(Status.PENDING.toString())) {
                connection.commit();
                return new TransactionStatusDto(transactionStatus);
            }
            // determine if transaction is negative or positive
            if (transaction.compareTo(BigDecimal.ZERO) > 0) {
                remainingAccountBalance = accountBalance.add(transaction).setScale(2, RoundingMode.HALF_UP);
                // set transaction status to ACCEPTED
                transactionStatus.setTransactionStatus(Status.ACCEPTED);
            } else {
                transaction = transaction.abs();
                remainingAccountBalance = accountBalance.subtract(transaction).setScale(2, RoundingMode.HALF_UP);
                // decline or approve
                if (remainingAccountBalance.compareTo(BigDecimal.ZERO) < 0) {
                    // set transaction status to DECLINED
                    transactionStatus.setTransactionStatus(Status.DECLINED);
                } else {
                    // set transaction status to ACCEPTED
                    transactionStatus.setTransactionStatus(Status.ACCEPTED);
                }
            }
            if (transactionStatus.getTransactionStatus().toString().equals(Status.ACCEPTED.toString())) {
                // update the balance for the account model and update the account repository
                account.setBalance(remainingAccountBalance);
                accountRepository.updateAccount(account, connection);
            }
            // update the transaction status table
            transactionStatusRepository.updateTransactionStatus(transactionStatus, connection);
            connection.commit();

        } catch (SQLException se) {
            logger.error("Could not update client Account: " + se.getMessage());
            try {
                connection.rollback();
            } catch (SQLException | NullPointerException se2) {

            }
            throw new DatabaseException("Could not update client Account", se);
        } finally {
            try {if (connection != null) { connection.close(); } } catch (SQLException sq3) { }
        }
        return new TransactionStatusDto(transactionStatus);
    }

}
