package com.mishas.stuff.ms.repository.dao;

import com.mishas.stuff.ms.repository.model.TransactionStatus;
import com.mishas.stuff.ms.utils.Status;
import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.jooq.SQLDialect.POSTGRES;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class TransactionStatusRepository {

    private static final Logger logger = Logger.getLogger(TransactionStatusRepository.class);

    // Create

    public Long createTransactionStatus(TransactionStatus transactionStatus, Connection connection) throws SQLException {
        PreparedStatement pst = null;
        ResultSet rs = null;
        Long id = null;
        if(logger.isDebugEnabled()) {
            logger.debug("creating a transaction status: " + transactionStatus.toString());
        }
        try {
            DSLContext create = DSL.using(connection, POSTGRES);
            // transaction status query
            pst = connection.prepareStatement(
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
            pst.setString(1, transactionStatus.getId());
            pst.setString(2, transactionStatus.getTransactionTimestamp().toString());
            pst.setString(3, transactionStatus.getTransactionStatus().toString());
            pst.executeQuery();
            if (rs.next()) {
                id = rs.getLong("ID");
            }
        } finally {
            try {if (rs != null) { rs.close(); } } catch (SQLException sq) {}
            try {if (pst != null) { pst.close(); } } catch (SQLException sq) {}
        }
        return id;
    }

    // Get

    public TransactionStatus getTransactionStatus(String id, Connection connection) throws SQLException {
        PreparedStatement pst = null;
        ResultSet rs = null;
        TransactionStatus transactionStatus;
        if(logger.isDebugEnabled()) {
            logger.debug("retrieving a transaction status with ID: " + id + " for an update");
        }
        try {
            DSLContext create = DSL.using(connection, POSTGRES);
            pst = connection.prepareStatement(
                    create.select().from(table("TRANSACTION_STATUS")).where(field("ID").eq(id)).getSQL()
            );
            pst.setString(1, id);
            rs = pst.executeQuery();
            if (rs.next()) {
                transactionStatus = new TransactionStatus(
                        rs.getString("ID"),
                        rs.getTimestamp("TRANSACTION_TIMESTAMP").toLocalDateTime(),
                        Status.valueOf(rs.getString("STATUS"))
                );
            } else {
                throw new SQLException("transaction status is null");
            }
        } finally {
            try {if (rs != null) { rs.close(); } } catch (SQLException sq) {}
            try {if (pst != null) { pst.close(); } } catch (SQLException sq2) {}
        }
        return transactionStatus;
    }

    // Get for Update

    public TransactionStatus getTransactionStatusForUpdate(String id, Connection connection) throws SQLException {
        PreparedStatement pst = null;
        ResultSet rs = null;
        TransactionStatus transactionStatus;
        if(logger.isDebugEnabled()) {
            logger.debug("retrieving a transaction status with ID: " + id + " for an update");
        }
        try {
            DSLContext create = DSL.using(connection, POSTGRES);
            pst = connection.prepareStatement(
                    create.select().from(table("TRANSACTION_STATUS")).where(field("ID").eq(id)).forUpdate().getSQL()
            );
            pst.setString(1, id);
            rs = pst.executeQuery();
            if (rs.next()) {
                transactionStatus = new TransactionStatus(
                        rs.getString("ID"),
                        rs.getTimestamp("TRANSACTION_TIMESTAMP").toLocalDateTime(),
                        Status.valueOf(rs.getString("STATUS"))
                );
            } else {
                throw new SQLException("transaction status is null");
            }
        } finally {
            try {if (rs != null) { rs.close(); } } catch (SQLException sq) {}
            try {if (pst != null) { pst.close(); } } catch (SQLException sq2) {}
        }
        return transactionStatus;
    }

    // Update Status

    public void updateTransactionStatus(TransactionStatus transactionStatus, Connection connection) throws SQLException {
        PreparedStatement pst = null;
        try {
            DSLContext create = DSL.using(connection, POSTGRES);
            if(logger.isDebugEnabled()) {
                logger.debug("updating transaction status with ID: " + transactionStatus.getId());
            }
            pst = connection.prepareStatement(
                    create.update(table("TRANSACTION_STATUS"))
                            .set(field("STATUS"), transactionStatus.getTransactionStatus().toString())
                            .where(field("ID").eq(transactionStatus.getId()))
                            .getSQL()
            );
            pst.setString(1, transactionStatus.getTransactionStatus().toString());
            pst.setString(2, transactionStatus.getId());
            pst.executeUpdate();
        } finally {
            try {if (pst != null) { pst.close(); } } catch (SQLException sq) {}
        }
    }
}
