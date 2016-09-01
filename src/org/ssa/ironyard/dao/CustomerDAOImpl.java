package org.ssa.ironyard.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.ssa.ironyard.model.Account;
import org.ssa.ironyard.model.Customer;

public class CustomerDAOImpl extends AbstractDAO<Customer>
{
    protected CustomerDAOImpl(DataSource datasource, ORM<Customer> orm)
    {
        super(datasource, orm);
    }

    // static final Logger LOGGER = LogManager.getLogger(CustomerDAOImpl.class);

    @Override
    public Customer insert(Customer customer)
    {
        Connection connection = null;
        PreparedStatement insert = null;
        ResultSet results = null;

        try
        {
            connection = datasource.getConnection();
            insert = connection.prepareStatement(this.orm.prepareInsert(), Statement.RETURN_GENERATED_KEYS);
            insert.setString(1, customer.getFirstName());
            insert.setString(2, customer.getLastName());
            insert.executeUpdate();
            results = insert.getGeneratedKeys();
            if (results.next())
            {
                Customer returnCustomer = new Customer(results.getInt(1), customer.getFirstName(),
                        customer.getLastName());

                return returnCustomer;
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
    public Customer update(Customer customer)
    {
        Connection connection = null;
        PreparedStatement update = null;

        try
        {
            connection = datasource.getConnection();
            update = connection.prepareStatement(orm.prepareUpdate(), Statement.RETURN_GENERATED_KEYS);
            update.setString(1, customer.getFirstName());
            update.setString(2, customer.getLastName());
            update.setInt(3, customer.getId());
            if (update.executeUpdate() > 0)
            {
                return customer;
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

    public List<Customer> read()
    {
        List<Customer> customers = new ArrayList<>();

        Connection connection = null;
        PreparedStatement readAll = null;
        ResultSet results = null;

        try
        {
            connection = datasource.getConnection();
            readAll = connection.prepareStatement(((CustomerORM) orm).prepareReadAll(),
                    Statement.RETURN_GENERATED_KEYS);
            results = readAll.executeQuery();
            while (results.next())
            {
                customers.add(this.orm.map(results));
            }
        }
        catch (Exception ex)
        {
        }
        finally
        {
            cleanup(results, readAll, connection);
        }
        return customers;
    }

    public List<Customer> readFirstName(String firstName)
    {
        return performSimpleStringQuery("first", firstName);
    }

    public List<Customer> readLastName(String lastName)
    {
        return performSimpleStringQuery("last", lastName);

    }

    private List<Customer> performSimpleStringQuery(String field, String fieldValue)
    {
        List<Customer> customers = new ArrayList<>();

        Connection connection = null;
        PreparedStatement queryString = null;
        ResultSet results = null;

        try
        {
            connection = datasource.getConnection();
            queryString = connection.prepareStatement(((CustomerORM) orm).prepareSimpleQuery(field));
            results = queryString.executeQuery();

            while (results.next())
            {
                customers.add(this.orm.map(results));
            }

            return customers;
        }
        catch (Exception ex)
        {
            return customers;
        }
        finally
        {
            cleanup(results, queryString, connection);
        }
    }

    public void clear()
    {
        Statement deleteAllData = null;
        Connection connection = null;
        try
        {
            connection = datasource.getConnection();
            deleteAllData = connection.createStatement();
            deleteAllData.execute("DELETE FROM customers");
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
