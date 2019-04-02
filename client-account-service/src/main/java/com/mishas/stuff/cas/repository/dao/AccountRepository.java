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
}
