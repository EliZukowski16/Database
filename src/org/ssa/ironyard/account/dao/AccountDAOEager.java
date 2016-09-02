package org.ssa.ironyard.account.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.ssa.ironyard.account.model.Account;
import org.ssa.ironyard.account.orm.AccountORM;
import org.ssa.ironyard.account.orm.AccountORMEager;
import org.ssa.ironyard.customer.model.Customer;

public class AccountDAOEager extends AbstractAccountDAO implements AccountDAO
{
    private Customer customer;

    protected AccountDAOEager(DataSource datasource)
    {
        super(datasource, new AccountORMEager());
    }

    @Override
    public Account read(int id)
    {
        Connection connection = null;
        PreparedStatement read = null;
        ResultSet query = null;

        try
        {
            connection = this.datasource.getConnection();
            read = connection.prepareStatement(((AccountORMEager)this.orm).prepareRead());
            read.setInt(1, id);
            query = read.executeQuery();
            if (query.next())
                this.customer = ((AccountORMEager) this.orm).mapCustomer(query);
                return ((AccountORMEager) this.orm).mapAccount(query);

        }
        catch (Exception ex)
        {

        }
        finally
        {
            cleanup(query, read, connection);
        }
        return null;
    }

    @Override
    public List<Account> readUser(int user)
    {
        List<Account> userAccounts = new ArrayList<>();

        Connection connection = null;
        PreparedStatement readUser = null;
        ResultSet results = null;

        try
        {
            connection = datasource.getConnection();
            readUser = connection.prepareStatement(((AccountORMEager)this.orm).prepareReadUser(),
                    Statement.RETURN_GENERATED_KEYS);
            readUser.setInt(1, user);
            results = readUser.executeQuery();
            while (results.next())
            {
                customer = ((AccountORMEager) this.orm).mapCustomer(results);
                userAccounts.add(((AccountORMEager) this.orm).mapAccount(results));
            }
        }
        catch (Exception ex)
        {
        }
        finally
        {
            cleanup(results, readUser, connection);
        }
        return userAccounts;
    }

    @Override
    public List<Account> readUnderwater()
    {
        List<Account> underwaterAccounts = new ArrayList<>();

        Connection connection = null;
        PreparedStatement readUnderwaterAccounts = null;
        ResultSet results = null;

        try
        {
            connection = datasource.getConnection();
            readUnderwaterAccounts = connection.prepareStatement(((AccountORMEager)this.orm).prepareReadUnderwater(),
                    Statement.RETURN_GENERATED_KEYS);
            results = readUnderwaterAccounts.executeQuery();
            while (results.next())
            {
                customer = ((AccountORMEager) this.orm).mapCustomer(results);
                underwaterAccounts.add(((AccountORMEager) this.orm).mapAccount(results));
            }
        }
        catch (Exception ex)
        {
        }
        finally
        {
            cleanup(results, readUnderwaterAccounts, connection);
        }
        return underwaterAccounts;
    }

    public Customer getCustomer()
    {
        return this.customer;
    }

}