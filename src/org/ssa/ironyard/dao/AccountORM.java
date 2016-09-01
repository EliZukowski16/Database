package org.ssa.ironyard.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.ssa.ironyard.model.Account;
import org.ssa.ironyard.model.Account.AccountType;
import org.ssa.ironyard.model.Customer;

public interface AccountORM extends ORM<Account>
{   
    default String projection()
    {
        return " id, customer, type, balance ";
    }

    default String table()
    {
        return " accounts ";
    }

    default Account map(ResultSet results) throws SQLException
    {
        return new Account(results.getInt("id"), new Customer(results.getInt("customer"), null, null), AccountType.getInstance(results.getString("type")), results.getBigDecimal("balance"));
    }

    default String prepareInsert()
    {
        return " INSERT INTO " + table() + " (customer, type, balance) VALUES (?,?,?) ";
    }

    default String prepareUpdate()
    {
        return " UPDATE " + table() + " SET customer = ?, type = ?, balance = ? WHERE id = ? ";
    }
    
    default String prepareReadUser()
    {
        return prepareSimpleQuery("customer");
    }
    
    default String prepareReadUnderwater()
    {
        return " SELECT " + projection() + " FROM " + table() + " WHERE balance < 0 ";
    }
}
