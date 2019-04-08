package com.mishas.stuff.ms.repository.dao;

import com.mishas.stuff.ms.repository.DataSource;
import com.mishas.stuff.ms.repository.model.IModel;
import com.mishas.stuff.ms.repository.model.Transaction;
import com.mishas.stuff.ms.repository.model.TransactionStatus;

import com.mishas.stuff.ms.utils.Status;
import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.jooq.SQLDialect.POSTGRES;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

import com.mishas.stuff.ms.utils.exceptions.DatabaseException;

public class TransactionDao {

    private static final Logger logger = Logger.getLogger(TransactionDao.class);

    // create a new transaction record

    public String createStatusRecord(TransactionStatus transactionStatus, Transaction transaction) {
        String correlationIdFromDb;
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement pstStatus = null;
        PreparedStatement pstTransaction = null;

        try {
            connection = DataSource.getConnection();
            DSLContext create = DSL.using(connection, POSTGRES);

            // transaction status query
            pstStatus = connection.prepareStatement(
                        create.insertInto(
                                table("TRANSACTION_STATUS"),
                                field("ID"),
                                field("TRANSACTION_TIMESTAMP"),
                                field("STATUS")
                        ).values(
                                transactionStatus.getId(),
                                transactionStatus.getTransactionTimestamp(),
                                transactionStatus.getTransactionStatus().toString()
                        ).returning(
                                field("ID")
                        ).getSQL()
            );

            // status params
            pstStatus.setString(1, transactionStatus.getId());
            pstStatus.setString(2, transactionStatus.getTransactionTimestamp().toString());
            pstStatus.setString(3, transactionStatus.getTransactionStatus().toString());

            // transaction query
            pstTransaction = connection.prepareStatement(
                    create.insertInto(
                            table("TRANSACTION"),
                           // field("ID"),
                            field("CORRELATION_ID"),
                            field("MASTERCARD_TRANSACTION_ID"),
                            field("TRANSACTION_AMOUNT"),
                            field("TRANSACTION_CURRENCY"),
                            field("CLIENT_FIRSTNAME"),
                            field("CLIENT_SURNAME"),
                            field("CLIENT_CARD_NUMBER")
                    ).values(
                           // transaction.getId(),
                            transaction.getCorrelationId(),
                            transaction.getMastercardTransactionId(),
                            transaction.getTransactionAmount(),
                            transaction.getTransactionCurrency(),
                            transaction.getFirstname(),
                            transaction.getSurname(),
                            transaction.getClientCardNumber()
                    ).getSQL()
            );

            rs = pstStatus.executeQuery();
            connection.commit();
            if(rs.next()) {
                    correlationIdFromDb = rs.getString("ID");
            } else {
                logger.error("Correlation ID is not present from the transaction status query, rolling back");
                connection.rollback();
                throw new DatabaseException("Transaction failed, could not record the transaction status to the database");
            }

            // record transaction paramerters
            pstTransaction.setString(1, correlationIdFromDb);
            pstTransaction.setString(2, transaction.getMastercardTransactionId());
            pstTransaction.setBigDecimal(3, transaction.getTransactionAmount());
            pstTransaction.setString(4, transaction.getTransactionCurrency());
            pstTransaction.setString(5, transaction.getFirstname());
            pstTransaction.setString(6, transaction.getSurname());
            pstTransaction.setString(7, transaction.getClientCardNumber());

            // execute the query
            int res = pstTransaction.executeUpdate();
            if (res < 1) {
                logger.error("Transaction update failed, rolling back");
                connection.rollback();
                throw new DatabaseException("Transaction failed, could not record the transaction to the database");
            }

            // commit the transaction
            connection.commit();

        } catch (SQLException sq1) {
            logger.error("insert failed, rolling back transaction " + sq1.getMessage());
            try {
                connection.rollback();
            } catch (SQLException sq2) {

            }
            throw new DatabaseException("insert failed, rolling back transaction", sq1);
        } finally {

            try {if (rs != null) { rs.close(); } } catch (SQLException sq4) { }
            try {if (pstStatus != null) { pstStatus.close(); } } catch (SQLException sq6) {}
            try {if (pstTransaction != null) { pstTransaction.close(); } } catch (SQLException s7) {}
            try {if (connection != null) { connection.close(); } } catch (SQLException sq8) { }
        }
        return  correlationIdFromDb;
    }

    // retrieve transaction record

    public List<IModel> getStatusRecord(String correlationId) {
        List<IModel> resultList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            connection = DataSource.getConnection();
            DSLContext create = DSL.using(connection, POSTGRES);
            pst = connection.prepareStatement(
                    create.select(
                            field("TRANSACTION_STATUS.ID").as("TRANSACTION_STATUS_ID"),
                            field("TRANSACTION_STATUS.TRANSACTION_TIMESTAMP"),
                            field("TRANSACTION_STATUS.STATUS"),
                            field("TRANSACTION.ID").as("TRANSACTION_ID"),
                            field("TRANSACTION.CORRELATION_ID"),
                            field("TRANSACTION.MASTERCARD_TRANSACTION_ID"),
                            field("TRANSACTION.TRANSACTION_AMOUNT"),
                            field("TRANSACTION.TRANSACTION_CURRENCY"),
                            field("TRANSACTION.CLIENT_FIRSTNAME"),
                            field("TRANSACTION.CLIENT_SURNAME"),
                            field("TRANSACTION.CLIENT_CARD_NUMBER")
                    )
                            .from(table("TRANSACTION_STATUS"))
                            .join(table("TRANSACTION"))
                            .on(field("TRANSACTION_STATUS.ID").eq(field("TRANSACTION.CORRELATION_ID")))
                            .where(field("TRANSACTION_STATUS.ID").eq(correlationId))
                            .getSQL()
            );
            pst.setString(1, correlationId);
            // excute uery
            rs = pst.executeQuery();
            if (rs.next()) {
                TransactionStatus transactionStatus = new TransactionStatus(
                        rs.getString("TRANSACTION_STATUS_ID"),
                        rs.getTimestamp("TRANSACTION_TIMESTAMP").toLocalDateTime(),
                        Status.valueOf(rs.getString("STATUS"))
                );
                Transaction transaction = new Transaction(
                        rs.getLong("TRANSACTION_ID"),
                        rs.getString("MASTERCARD_TRANSACTION_ID"),
                        rs.getString("CORRELATION_ID"),
                        rs.getBigDecimal("TRANSACTION_AMOUNT"),
                        rs.getString("TRANSACTION_CURRENCY"),
                        rs.getString("CLIENT_FIRSTNAME"),
                        rs.getString("CLIENT_SURNAME"),
                        rs.getString("CLIENT_CARD_NUMBER")
                );
                resultList.add(transactionStatus);
                resultList.add(transaction);
            }
            connection.commit();

        } catch (SQLException sq1) {
            logger.error("select query failed, rolling back transaction " + sq1.getMessage());
            try {
                connection.rollback();
            } catch (SQLException sq2){

            }
            throw new DatabaseException("database query failed, rolling back transaction", sq1);
        } finally {
            try {if (rs != null) { rs.close(); } } catch (SQLException sq4) { }
            try {if (pst != null) { pst.close(); } } catch (SQLException s5) {}
            try {if (connection != null) { connection.close(); } } catch (SQLException sq6) { }
        }
        return resultList;
    }

    // update transaction record

    public void updateStatusRecord(String correlationId, TransactionStatus transactionStatus)  {

        TransactionStatus updatedTransactionStatus = null;
        Connection connection = null;
        PreparedStatement selectStatement = null;
        PreparedStatement updateStatement = null;
        ResultSet rsSelect = null;

        try {
            connection = DataSource.getConnection();
            DSLContext create = DSL.using(connection, POSTGRES);

            // query for update
            selectStatement = connection.prepareStatement(
                    create.select()
                            .from(table("TRANSACTION_STATUS"))
                            .where(field("ID").eq(correlationId))
                           // .forUpdate()
                            .getSQL()
            );
            // perform update
            updateStatement = connection.prepareStatement(
                    create.update(table("TRANSACTION_STATUS"))
                            .set(field("STATUS"), transactionStatus.getTransactionStatus().toString())
                            .where(field("ID").eq(correlationId))
                            .getSQL()
            );
            selectStatement.setString(1, correlationId);
            updateStatement.setString(1, transactionStatus.getTransactionStatus().toString());
            updateStatement.setString(2, correlationId);

            // execute queries
            rsSelect = selectStatement.executeQuery();
            // if empty then rollback
            if (!rsSelect.next()) {
                connection.rollback();
                logger.error("Record with id: " + correlationId + " doesn't exist, failed to perform the update, rolling back the transaction");
            }
            // if  exists then update the status of transaction
            updateStatement.executeUpdate();
            // commit transaction
            connection.commit();

        } catch (SQLException sq1) {
            logger.error("failed to update, rolling back the transaction, reason: " + sq1.getMessage());
            try {
                connection.rollback();
                throw new DatabaseException(sq1.getMessage(), sq1);
            } catch (SQLException sq2) {
                logger.error("failed to rollback transaction, reason: " + sq2.getMessage());
                throw new DatabaseException(sq2.getMessage(), sq2);
            }

        } finally {
            try {if (rsSelect != null) { rsSelect.close(); } } catch (SQLException sq4) { }
            try {if (selectStatement != null) { selectStatement.close(); } } catch (SQLException sq6) {}
            try {if (updateStatement != null) { updateStatement.close(); } } catch (SQLException s7) {}
            try {if (connection != null) { connection.close(); } } catch (SQLException sq8) { }
        }
    }
}
