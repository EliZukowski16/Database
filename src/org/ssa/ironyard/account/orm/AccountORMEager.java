package org.ssa.ironyard.account.orm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.ssa.ironyard.account.model.Account;
import org.ssa.ironyard.account.model.Account.AccountType;
import org.ssa.ironyard.customer.model.Customer;

public class AccountORMEager implements AccountORM
{

    @Override
    public String projection()
    {
        return "accounts.id, accounts.customer, accounts.type, accounts.balance, customers.first, customers.last";
    }
    
    public Account mapAccount(ResultSet results) throws SQLException
    {
        return new Account(results.getInt("accounts.id"), new Customer(results.getInt("accounts.customer"), null, null), AccountType.getInstance(results.getString("accounts.type")), results.getBigDecimal("accounts.balance"), true);
    }
    
    public Customer mapCustomer(ResultSet results) throws SQLException
    {
        return new Customer(results.getInt("accounts.customer"), results.getString("customers.first"), results.getString("customers.last"), true);
    }
    
    @Override
    public String prepareRead()
    {
        return " SELECT " + projection() + " FROM " + join() + " ON " + relationship() + " WHERE accounts.id = ? ";
        
    }
    
    @Override
    public String prepareReadUser()
    {
        return " SELECT " + projection() + " FROM " + join() + " ON " + relationship() + " WHERE accounts.customer = ? ";
    }
    
    @Override
    public String prepareReadUnderwater()
    {
        return " SELECT " + projection() + " FROM " + join() + " ON " + relationship() + " WHERE accounts.balance < ? ";
    }
    
    private String join()
    {
        return " accounts JOIN customers ";
    }
    
    private String relationship()
    {
        return " accounts.customer = customers.id ";
    }

}
