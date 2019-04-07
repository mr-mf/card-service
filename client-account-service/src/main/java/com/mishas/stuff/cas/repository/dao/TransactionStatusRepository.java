package com.mishas.stuff.cas.repository.dao;

import com.mishas.stuff.cas.repository.model.TransactionStatus;
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

    public void createTransactionStatus(TransactionStatus transactionStatus, Connection connection) throws SQLException {
        PreparedStatement pst = null;
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
                    ).getSQL()
            );
            // status params
            pst.setString(1, transactionStatus.getId());
            pst.setString(2, transactionStatus.getTransactionTimestamp().toString());
            pst.setString(3, transactionStatus.getTransactionStatus().toString());
            pst.executeUpdate();
        } finally {
            try {if (pst != null) { pst.close(); } } catch (SQLException sq) {}
        }
    }

    public TransactionStatus getTransactionStatus(String id, Connection connection) {
        return null;
    }

    public TransactionStatus getTransactionStatusForUpdate(String id, Connection connection) {
        return null;
    }

    public void updateTransactionStatus(TransactionStatus transactionStatus, Connection connection) throws SQLException {
        PreparedStatement pst = null;
        try {
            DSLContext create = DSL.using(connection, POSTGRES);
            // perform update
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
