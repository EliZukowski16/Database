package org.ssa.ironyard.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.ssa.ironyard.model.Account;

public class AccountDAOImpl extends AbstractDAO<Account>
{   
    protected AccountDAOImpl(DataSource datasource, ORM<Account> orm)
    {
        super(datasource, orm);
        // TODO Auto-generated constructor stub
    }

//    static final Logger LOGGER = LogManager.getLogger(CustomerDAOImpl.class);

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
            insert.setInt(1, account.getCustomerId());
            insert.setString(2, account.getType().getAccountType());
            insert.setDouble(3, account.getBalance());
            insert.executeUpdate();
            results = insert.getGeneratedKeys();
            if(results.next())
            {
                Account returnAccount = new Account(results.getInt(1), account.getCustomerId(), account.getType(), account.getBalance());
            
                return returnAccount;
            }
        }
        catch(Exception ex)
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
            update.setInt(1, account.getCustomerId());
            update.setString(2, account.getType().getAccountType());
            update.setDouble(3, account.getBalance());
            update.setInt(4, account.getId());
            if (update.executeUpdate() > 0)
            {
                return account;
            }
        }
        catch(Exception ex)
        {
            
        }
        finally
        {
            cleanup(update, connection);
        }
        return null;
    }

    public void clear()
    {
        Statement deleteAllData = null;
        Connection connection = null;
        try
        {
            connection = datasource.getConnection();
            deleteAllData = connection.createStatement();
            deleteAllData.execute("DELETE FROM accounts");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            cleanup(deleteAllData, connection);
        }

    }
}