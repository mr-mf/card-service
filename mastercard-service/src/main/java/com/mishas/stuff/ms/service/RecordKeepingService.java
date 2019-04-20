package com.mishas.stuff.ms.service;

import com.mishas.stuff.ms.repository.DataSource;
import com.mishas.stuff.ms.repository.dao.TransactionRepository;
import com.mishas.stuff.ms.repository.dao.TransactionStatusRepository;
import com.mishas.stuff.ms.repository.model.Transaction;
import com.mishas.stuff.ms.repository.model.TransactionStatus;
import com.mishas.stuff.ms.utils.Status;
import com.mishas.stuff.ms.utils.exceptions.DatabaseException;
import com.mishas.stuff.ms.web.dto.IDto;
import com.mishas.stuff.ms.web.dto.TransactionDto;
import com.mishas.stuff.ms.web.dto.TransactionStatusDto;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RecordKeepingService implements IRecordKeepingService {

    private static final Logger logger = Logger.getLogger(RecordKeepingService.class);
    private TransactionRepository transactionDao;
    private TransactionStatusRepository transactionStatusRepository;
    private ITransactionApprovalService transactionApprovalService;

    public RecordKeepingService(
            TransactionRepository transactionRepository,
            ITransactionApprovalService transactionApprovalService,
            TransactionStatusRepository transactionStatusRepository) {
        this.transactionDao = transactionRepository;
        this.transactionApprovalService = transactionApprovalService;
        this.transactionStatusRepository = transactionStatusRepository;
    }

    // api

    // Create

    @Override
    public TransactionStatusDto createTransaction(TransactionDto transactionDto) {
        Connection connection = null;
        String CorrelationIDFronDb = null;

        TransactionStatusDto transactionStatusDto = createCorrelationID(transactionDto);
        TransactionStatus transactionStatus = new TransactionStatus(transactionStatusDto);
        String correlationId = transactionStatusDto.getId();
        transactionDto.setCorrelationId(correlationId);
        // add correlation id to transaction model
        Transaction transaction = new Transaction(transactionDto);

        try {
            connection = DataSource.getConnection();
            try {
                CorrelationIDFronDb = transactionStatusRepository.createTransactionStatus(transactionStatus, connection);
            } catch (SQLException sqIgnore) {
                logger.info("Could not create transaction status with correlation ID: " + correlationId + " transaction already exist");
                connection.rollback();
            }
            // get transaction status for update, so it cant be queried while in transit
            transactionStatus = transactionStatusRepository.getTransactionStatusForUpdate(correlationId, connection);
            if (CorrelationIDFronDb != null) {
                transactionDao.createTransaction(transaction, connection);
            } else if (!transactionStatus.getTransactionStatus().toString().equals(Status.PENDING.toString())) {
                connection.commit();
                return new TransactionStatusDto(transactionStatus);
            }
            transactionStatusDto = transactionApprovalService.sendTransactionForApproval(transactionDto);
            transactionStatusRepository.updateTransactionStatus(new TransactionStatus(transactionStatusDto), connection);
            connection.commit();
        } catch (SQLException sq) {
            logger.error("Could not create transaction with correlation ID: " + correlationId + " reason: " + sq.getMessage());
            try {
                connection.rollback();
            } catch (SQLException | NullPointerException e) {
                e.printStackTrace();
            }
            throw new DatabaseException("Could not create transaction with correlation ID: " + correlationId, sq);
        } finally {
            try {if (connection != null) { connection.rollback(); connection.close(); } } catch (SQLException sq) { }
        }
        return transactionStatusDto;
    }

    // Get

    @Override
    public List <IDto> getTransaction(String correlationId) {
        List <IDto> dtoList = new ArrayList<>();
        Connection connection = null;
        TransactionStatus transactionStatus = null;
        Transaction transaction = null;
        try {
            connection = DataSource.getConnection();
            transactionStatusRepository.getTransactionStatus(correlationId, connection);
            transactionDao.getTransaction(correlationId, connection);
            connection.commit();
        } catch (SQLException sq) {
            logger.error("Could not get the transaction with correlation ID: " + correlationId + " error: " + sq.getMessage());
            try {
                connection.rollback();
            } catch (SQLException | NullPointerException e) {
            }
            throw new DatabaseException("Could not get the transaction with correlation ID: " + correlationId, sq);
        } finally {
            try {if (connection != null) { connection.rollback(); connection.close(); } } catch (SQLException sq) { }
        }
        TransactionStatusDto transactionStatusDto = new TransactionStatusDto(transactionStatus);
        TransactionDto transactionDto = new TransactionDto(transaction);
        dtoList.add(transactionStatusDto);
        dtoList.add(transactionDto);
        return dtoList;
    }

    // private api

    private TransactionStatusDto createCorrelationID(TransactionDto transactionDto) {
        String source = transactionDto.toString();
        Optional<UUID> uuid = null;
        try {
            byte[] bytes = source.getBytes("UTF-8");
            uuid = Optional.of(UUID.nameUUIDFromBytes(bytes));
        } catch (UnsupportedEncodingException ue) {
            logger.error("Cannot create UUID: {}", ue.getCause());
        }
        return new TransactionStatusDto(uuid.get().toString(), LocalDateTime.now());
    }

}
