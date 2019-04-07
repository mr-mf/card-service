package com.mishas.stuff.cas.repository.dao;

import com.mishas.stuff.cas.repository.DataSource;
import com.mishas.stuff.cas.repository.model.Account;
import com.mishas.stuff.cas.utils.exceptions.DatabaseException;
import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class AccountRepository {

    private static final Logger logger = Logger.getLogger(AccountRepository.class);

    public Long createAccount(Account account) {
        Long id = null;
        Connection connection = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        // create account

        try {
            connection = DataSource.getConnection();
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
            pst = connection.prepareStatement(
                    create.insertInto(
                            table("ACCOUNT"),
                            field("BALANCE"),
                            field("CURRENCY"),
                            field("CARD_NUMBER")
                    ).values(
                            account.getBalance(),
                            account.getCurrency(),
                            account.getCardNumber()
                    ).returning(field("ID")
                    ).getSQL()
            );
            pst.setBigDecimal(1, account.getBalance());
            pst.setString(2, account.getCurrency());
            pst.setString(3, account.getCardNumber());
            // execute query
            rs = pst.executeQuery();
            if (rs.next()) {
                id = rs.getLong("ID");
            }
            connection.commit();

        } catch (SQLException sq1) {
            logger.error("Could not create and account: " + sq1);
            try {
                connection.rollback();
            } catch (SQLException | NullPointerException se2) {
                // ignore
            }
            throw new DatabaseException("Could not create and account", sq1);

        } finally {
            try {if (rs != null) { rs.close(); } } catch (SQLException sq3) { }
            try {if (pst != null) { pst.close(); } } catch (SQLException sq4) { }
            try {if (connection != null) { connection.close(); } } catch (SQLException sq5) { }
        }
        return id;
    }

    // get account

    public Account getAccount(Long id) {
        Account account = null;
        Connection connection = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            connection = DataSource.getConnection();
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
            pst = connection.prepareStatement(
                    create.select(
                            field("ID"),
                            field("BALANCE"),
                            field("CURRENCY"),
                            field("CARD_NUMBER")
                    )
                            .from(table("ACCOUNT"))
                            .where(field("ID").eq(id))
                            .getSQL()
            );
            pst.setLong(1, id);
            // execute query
            rs = pst.executeQuery();
            if (rs.next()) {
                account = new Account(
                        rs.getLong("ID"),
                        rs.getBigDecimal("BALANCE"),
                        rs.getString("CURRENCY"),
                        rs.getString("CARD_NUMBER")
                );
            }
            connection.commit();

        } catch (SQLException se1) {
            logger.error("Could not create and account: " + se1);
            try {
                connection.rollback();
            } catch (SQLException | NullPointerException se2) {
                // ignore
            }
            throw new DatabaseException("Could not create and account", se1);

        } finally {
            try {if (rs != null) { rs.close(); } } catch (SQLException sq3) { }
            try {if (pst != null) { pst.close(); } } catch (SQLException sq4) { }
            try {if (connection != null) { connection.close(); } } catch (SQLException sq5) { }
        }
        return account;
    }

    // get account for update

    public Account getAccountForUpdate(String creditCardNumber, Connection connection) throws SQLException {
        PreparedStatement pst = null;
        ResultSet rs = null;
        Account account = null;
        try {
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
            pst = connection.prepareStatement(
                    create.select(
                            field("ID"),
                            field("BALANCE"),
                            field("CURRENCY"),
                            field("CARD_NUMBER")
                    )
                            .from(table("ACCOUNT"))
                            .where(field("CARD_NUMBER").eq(creditCardNumber))
                            .forUpdate()
                            .getSQL()
            );
            pst.setString(1, creditCardNumber);
            // execute query
            rs = pst.executeQuery();
            if (!rs.next()) {
                // have to kill it because it will be used as part of transaction
                throw new SQLException("Bank account for the credit card: " + creditCardNumber + " does not exist");
            } else {
                account = new Account(
                        rs.getLong("ID"),
                        rs.getBigDecimal("BALANCE"),
                        rs.getString("CURRENCY"),
                        rs.getString("CARD_NUMBER")
                );
            }
        } finally {
            try {if (pst != null) { pst.close(); } } catch (SQLException sq1) {}
            try {if (rs != null) { rs.close(); } } catch (SQLException sq2) {}
        }
        return account;
    }

    // update the account

    public void updateAccount(Account account, Connection connection) throws SQLException {
        PreparedStatement pst = null;
        try {
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
            pst = connection.prepareStatement(
                    create.update(table("ACCOUNT"))
                            .set(field("BALANCE"), account.getBalance())
                            .where(field("ID").eq(account.getClientId()))
                            .getSQL()
            );
            pst.setBigDecimal(1, account.getBalance());
            pst.setLong(2, account.getClientId());
            pst.executeUpdate();
        } finally {
            try {if (pst != null) { pst.close(); } } catch (SQLException sq) {}
        }
    }
}
