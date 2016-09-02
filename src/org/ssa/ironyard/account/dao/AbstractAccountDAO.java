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
import org.ssa.ironyard.account.orm.AccountORMImpl;
import org.ssa.ironyard.dao.AbstractDAO;
import org.ssa.ironyard.orm.ORM;

public abstract class AbstractAccountDAO extends AbstractDAO<Account>
{

    protected AbstractAccountDAO(DataSource datasource, ORM<Account> orm)
    {
        super(datasource, orm);
    }
    
    protected AbstractAccountDAO(DataSource datasource)
    {
        this(datasource, new AccountORMImpl());
    }

    @Override
    public Account insert(Account account)
    {
        Connection connection = null;
        PreparedStatement insert = null;
        ResultSet results = null;

        try
        {
            connection = datasource.getConnection();
            insert = connection.prepareStatement(this.orm.prepareInsert(), Statement.RETURN_GENERATED_KEYS);
            insert.setInt(1, account.getCustomer().getId());
            insert.setString(2, account.getType().getAccountType());
            insert.setBigDecimal(3, account.getBalance());
            insert.executeUpdate();
            results = insert.getGeneratedKeys();
            if (results.next())
            {
                Account returnAccount = new Account(results.getInt(1), account.getCustomer(), account.getType(),
                        account.getBalance(), true);

                return returnAccount;
            }
        }
        catch (Exception ex)
        {
        }
        finally
        {
            cleanup(results, insert, connection);
        }
        return null;
    }

    @Override
    public Account update(Account account)
    {
        Connection connection = null;
        PreparedStatement update = null;

        try
        {
            connection = datasource.getConnection();
            update = connection.prepareStatement(orm.prepareUpdate(), Statement.RETURN_GENERATED_KEYS);
            update.setInt(1, account.getCustomer().getId());
            update.setString(2, account.getType().getAccountType());
            update.setBigDecimal(3, account.getBalance());
            update.setInt(4, account.getId());
            if (update.executeUpdate() > 0)
            {
                Account accountCopy = account.clone();
                accountCopy.setLoaded(true);
                return accountCopy;
            }
        }
        catch (Exception ex)
        {

        }
        finally
        {
            cleanup(update, connection);
        }
        return null;
    }

    public List<Account> readUser(int user)
    {
        List<Account> userAccounts = new ArrayList<>();

        Connection connection = null;
        PreparedStatement readUser = null;
        ResultSet results = null;

        try
        {
            connection = datasource.getConnection();
            readUser = connection.prepareStatement(((AccountORM) orm).prepareReadUser(),
                    Statement.RETURN_GENERATED_KEYS);
            readUser.setInt(1, user);
            results = readUser.executeQuery();
            while (results.next())
            {
                userAccounts.add(this.orm.map(results));
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

    public List<Account> readUnderwater()
    {
        List<Account> underwaterAccounts = new ArrayList<>();

        Connection connection = null;
        PreparedStatement readUnderwaterAccounts = null;
        ResultSet results = null;

        try
        {
            connection = datasource.getConnection();
            readUnderwaterAccounts = connection.prepareStatement(((AccountORM) orm).prepareReadUnderwater(),
                    Statement.RETURN_GENERATED_KEYS);
            readUnderwaterAccounts.setInt(1, 0);
            results = readUnderwaterAccounts.executeQuery();
            while (results.next())
            {
                underwaterAccounts.add(this.orm.map(results));
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

}
