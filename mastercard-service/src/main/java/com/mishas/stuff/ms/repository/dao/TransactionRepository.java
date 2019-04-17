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

public class TransactionRepository {

    private static final Logger logger = Logger.getLogger(TransactionRepository.class);

    // create a new transaction record

    public Long createTransaction(Transaction transaction, Connection connection) throws SQLException {
        Long id = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            DSLContext create = DSL.using(connection, POSTGRES);
            // transaction query
            pst = connection.prepareStatement(
                    create.insertInto(
                            table("TRANSACTION"),
                            field("CORRELATION_ID"),
                            field("MASTERCARD_TRANSACTION_ID"),
                            field("TRANSACTION_AMOUNT"),
                            field("TRANSACTION_CURRENCY"),
                            field("CLIENT_FIRSTNAME"),
                            field("CLIENT_SURNAME"),
                            field("CLIENT_CARD_NUMBER")
                    ).values(
                            transaction.getCorrelationId(),
                            transaction.getMastercardTransactionId(),
                            transaction.getTransactionAmount(),
                            transaction.getTransactionCurrency(),
                            transaction.getFirstname(),
                            transaction.getSurname(),
                            transaction.getClientCardNumber()
                    ).returning(
                            field("ID")
                    ).getSQL()
            );
            // record transaction paramerters
            pst.setString(1, transaction.getCorrelationId());
            pst.setString(2, transaction.getMastercardTransactionId());
            pst.setBigDecimal(3, transaction.getTransactionAmount());
            pst.setString(4, transaction.getTransactionCurrency());
            pst.setString(5, transaction.getFirstname());
            pst.setString(6, transaction.getSurname());
            pst.setString(7, transaction.getClientCardNumber());

            // execute the query
            rs = pst.executeQuery();
            if (rs.next()) {
                id = rs.getLong("ID");
            } else {
                logger.error("Transaction update failed, rolling back");
                throw new SQLException("Transaction failed, could not record the transaction to the database");
            }
        } finally {
            try {if (pst != null) { pst.close(); } } catch (SQLException se) {}
            try {if (rs != null) { rs.close(); } } catch (SQLException sq4) { }
        }
        return id;
    }

    // get transaction record

    public Transaction getTransaction(String correlationId, Connection connection) throws SQLException {
        Transaction transaction = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            DSLContext create = DSL.using(connection, POSTGRES);
            pst = connection.prepareStatement(
                    create.select(
                            field("ID"),
                            field("CORRELATION_ID"),
                            field("MASTERCARD_TRANSACTION_ID"),
                            field("TRANSACTION_AMOUNT"),
                            field("TRANSACTION_CURRENCY"),
                            field("CLIENT_FIRSTNAME"),
                            field("CLIENT_SURNAME"),
                            field("CLIENT_CARD_NUMBER")
                    )
                            .from(table("TRANSACTION"))
                            .where(field("CORRELATION_ID").eq(correlationId))
                            .getSQL()
            );
            pst.setString(1, correlationId);
            // excute uery
            rs = pst.executeQuery();
            if (rs.next()) {
                transaction = new Transaction(
                        rs.getLong("ID"),
                        rs.getString("MASTERCARD_TRANSACTION_ID"),
                        rs.getString("CORRELATION_ID"),
                        rs.getBigDecimal("TRANSACTION_AMOUNT"),
                        rs.getString("TRANSACTION_CURRENCY"),
                        rs.getString("CLIENT_FIRSTNAME"),
                        rs.getString("CLIENT_SURNAME"),
                        rs.getString("CLIENT_CARD_NUMBER")
                );
            }
        } finally {
            try {if (rs != null) { rs.close(); } } catch (SQLException sq) { }
            try {if (pst != null) { pst.close(); } } catch (SQLException s) {}
        }
        return transaction;
    }

    // get transaction for update

    public Transaction getTransactionForUpdate(String correlationId, Connection connection) throws SQLException {

    }
}
