package com.mishas.stuff.ms.service;

import com.mishas.stuff.ms.repository.dao.TransactionDao;
import com.mishas.stuff.ms.repository.model.IModel;
import com.mishas.stuff.ms.repository.model.Transaction;
import com.mishas.stuff.ms.repository.model.TransactionStatus;
import com.mishas.stuff.ms.utils.exceptions.DatabaseException;
import com.mishas.stuff.ms.web.dto.IDto;
import com.mishas.stuff.ms.web.dto.TransactionDto;
import com.mishas.stuff.ms.web.dto.TransactionStatusDto;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RecordKeepingService implements IRecordKeepingService {

    private static final Logger logger = Logger.getLogger(RecordKeepingService.class);
    private TransactionDao transactionDao;

    public RecordKeepingService(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    // api

    // Create

    @Override
    public void createTransaction(TransactionDto transactionDto) {
        String correlationId;
        TransactionStatusDto transactionStatusDto = createCorrelationID(transactionDto);
        TransactionStatus transactionStatus = new TransactionStatus(transactionStatusDto);
        Transaction transaction = new Transaction(transactionDto);

        correlationId = transactionDao.createStatusRecord(transactionStatus, transaction);
    }

    // Get

    @Override
    public List <IDto> getTransaction(String correlationId) {
        List <IDto> dtoList = new ArrayList<>();
        List<IModel> list = transactionDao.getStatusRecord(correlationId);
        TransactionStatusDto transactionStatusDto = new TransactionStatusDto( (TransactionStatus) list.get(0) );
        TransactionDto transactionDto = new TransactionDto( (Transaction) list.get(1) );

        dtoList.add(transactionStatusDto);
        dtoList.add(transactionDto);
        return dtoList;
    }

    // Update

    @Override
    public void updateTransaction(String correlationId, TransactionStatusDto transactionStatusDto) {
        transactionDao.updateStatusRecord(correlationId, new TransactionStatus(transactionStatusDto));
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
