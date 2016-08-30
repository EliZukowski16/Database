package org.ssa.ironyard.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

public class CustomerDAOImpl implements CustomerDAO
{
    private DataSource dataSource;
    private Connection connection;

    private enum StatementType
    {
        QUERY, INSERT, UPDATE, DELETE;
    }

    public CustomerDAOImpl(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    @Override
    public Customer insert(Customer customer)
    {
        return executePreparedStatement(StatementType.INSERT, customer);
    }

    @Override
    public boolean delete(Customer toDelete)
    {
       Customer deletedCustomer = executePreparedStatement(StatementType.DELETE, toDelete);
       
       if(deletedCustomer != null && deletedCustomer.equals(toDelete))
           return true;
       return false;
    }

    @Override
    public Customer update(Customer customer)
    {
        return executePreparedStatement(StatementType.UPDATE, customer);
    }

    @Override
    public Customer read(int id)
    {
        return executePreparedStatement(StatementType.QUERY, new Customer(id, null, null));
    }

    private Customer executePreparedStatement(StatementType type, Customer customer)
    {
        PreparedStatement statement;
        ResultSet results = null;

        try
        {
            this.connection = dataSource.getConnection();
            Customer returnCustomer;
            switch (type)
            {
            case QUERY:
                statement = this.connection.prepareStatement("SELECT * FROM customers WHERE id = ?");
                statement.setInt(1, customer.getId());
                results = statement.executeQuery();
                results.next();
                returnCustomer =  new Customer(results.getInt(1), results.getString(2), results.getString(3));
                this.connection.close();
                return returnCustomer;
            case DELETE:
                statement = this.connection.prepareStatement("DELETE FROM customers WHERE id = ?",
                        Statement.RETURN_GENERATED_KEYS);
                statement.setInt(1, customer.getId());
                if(statement.executeUpdate() == 1)
                {
                    this.connection.close();
                    return customer;
                }
                else
                {
                    this.connection.close();
                    return null;
                }
            case INSERT:
                statement = this.connection.prepareStatement("INSERT INTO customers (first, last) VALUES (?,?)",
                        Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, customer.getFirstName());
                statement.setString(2, customer.getLastName());
                statement.executeUpdate();
                results = statement.getGeneratedKeys();
                results.next();
                return read(results.getInt(1));
            case UPDATE:
                statement = this.connection.prepareStatement("UPDATE customers SET first = ?, last = ? WHERE id = ?",
                        Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, customer.getFirstName());
                statement.setString(2, customer.getLastName());
                statement.setInt(3, customer.getId());
                statement.executeUpdate();
                return read(customer.getId());
            default:
                this.connection.close();
                return null;
            }

        }
        catch (IllegalArgumentException | SQLException sqex)
        {
            return null;
        }
    }

}
