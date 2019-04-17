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
        Connection connection = null;
        try {
            connection = DataSource.getConnection();
            accountRepository.createAccount(account, connection);
            connection.commit();
        } catch (SQLException sq) {
            logger.error("could not create an account: " + sq.getMessage());
            try {
                connection.rollback();
            } catch (SQLException | NullPointerException sq2) {
            }
            throw new DatabaseException("Could not create a new Account", sq);
        } finally {
            try {if (connection != null) { connection.close(); } } catch (SQLException sq3) { }
        }
        return null;
    }

    @Override
    public AccountDto getAccount(Long id) {
        Connection connection = null;
        Account account = null;
        try {
            connection = DataSource.getConnection();
            account = accountRepository.getAccount(id, connection);
            connection.commit();
        } catch (SQLException sq) {
            logger.error("could not retrieve an account: " + sq.getMessage());
            try {
                connection.rollback();
            } catch (SQLException | NullPointerException sq2) {
            }
            throw new DatabaseException("could not retrieve an account: ", sq);
        } finally {
            try {if (connection != null) { connection.close(); } } catch (SQLException sq3) { }
        }
        return new AccountDto(account);
    }

    @Override
    public TransactionStatusDto updateAccount(TransactionDto transactionDto) {
        TransactionStatus transactionStatus;
        Connection connection = null;
        try {
            logger.info("Starting account service with transaction: " + transactionDto.toString());
            connection = DataSource.getConnection();
            Account account = accountRepository.getAccountForUpdate(transactionDto.getClientCardNumber(), connection);
            // get the account balance and the transaction amount and calculate the difference
            BigDecimal accountBalance = account.getBalance().setScale(2, RoundingMode.HALF_UP);
            BigDecimal transactionAmount = transactionDto.getTransactionAmount().setScale(2, RoundingMode.HALF_UP);
            BigDecimal remainingAccountBalance;
            transactionStatus = new TransactionStatus(transactionDto.getCorrelationId(), LocalDateTime.now(), Status.PENDING);
            // update the transaction status table
            try {
                transactionStatusRepository.createTransactionStatus(transactionStatus, connection);
            } catch (SQLException seIgnore) {
                // ignore
                if (logger.isDebugEnabled()) {
                    logger.warn("Transaction status already exists: " + seIgnore.getMessage());
                }
                connection.rollback();
            }
            transactionStatus = transactionStatusRepository.getTransactionStatusForUpdate(transactionStatus.getId(), connection);
            if (!transactionStatus.getTransactionStatus().toString().equals(Status.PENDING.toString())) {
                if (logger.isDebugEnabled()) {
                    logger.info("Transaction already exists, has status " + transactionStatus.getTransactionStatus());
                }
                connection.commit();
                return new TransactionStatusDto(transactionStatus);
            }
            // determine if transaction is negative or positive
            if (transactionAmount.compareTo(BigDecimal.ZERO) > 0) {
                remainingAccountBalance = accountBalance.add(transactionAmount).setScale(2, RoundingMode.HALF_UP);
                // set transaction status to ACCEPTED
                transactionStatus.setTransactionStatus(Status.ACCEPTED);
                if (logger.isDebugEnabled()) {
                    logger.debug("Transaction accepted");
                }
            } else {
                transactionAmount = transactionAmount.abs();
                remainingAccountBalance = accountBalance.subtract(transactionAmount).setScale(2, RoundingMode.HALF_UP);
                // decline or approve
                if (remainingAccountBalance.compareTo(BigDecimal.ZERO) < 0) {
                    // set transaction status to DECLINED
                    transactionStatus.setTransactionStatus(Status.DECLINED);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Transaction declined");
                    }
                } else {
                    // set transaction status to ACCEPTED
                    transactionStatus.setTransactionStatus(Status.ACCEPTED);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Transaction accepted");
                    }
                }
            }
            if (transactionStatus.getTransactionStatus().toString().equals(Status.ACCEPTED.toString())) {
                // update the balance for the account model and update the account repository
                account.setBalance(remainingAccountBalance);
                accountRepository.updateAccount(account, connection);
                logger.info("account balance updated by" );
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
