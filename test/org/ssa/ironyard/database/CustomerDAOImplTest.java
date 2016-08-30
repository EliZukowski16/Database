package org.ssa.ironyard.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mysql.cj.jdbc.MysqlDataSource;

public class CustomerDAOImplTest
{
    static String URL = "jdbc:mysql://localhost/ssa_bank?user=root&password=root";
    DataSource dataSource;
    Connection connection;
    CustomerDAOImpl customerDAO;
    Customer testCustomer;

    @Before
    public void setupDB() throws SQLException
    {
        MysqlDataSource mysqlDdataSource = new MysqlDataSource();
        mysqlDdataSource.setURL(URL);
        
        this.connection = mysqlDdataSource.getConnection();
        this.dataSource = mysqlDdataSource;
        
        this.customerDAO = new CustomerDAOImpl(dataSource);
        
        testCustomer = new Customer("John", "Doe");
    }
    
    @After
    public void teardown() throws SQLException
    {
        Statement deleteAllData = connection.createStatement();
        deleteAllData.execute("DELETE FROM customers");
        
        this.connection.close();
    }
    
    @Test
    public void insertCustomerIntoDB() throws SQLException
    {   
        Customer testCustomerInDB = customerDAO.insert(testCustomer);
        
        assertNotEquals(testCustomer, testCustomerInDB);
        assertEquals(testCustomer.getFirstName(), testCustomerInDB.getFirstName());
        assertEquals(testCustomer.getLastName(), testCustomerInDB.getLastName());
        
        ResultSet results = executePreparedStatement(StatementType.QUERY, testCustomerInDB);
        
        assertTrue(results.next());
        assertEquals(new Customer(results.getInt(1), results.getString(2), results.getString(3)), testCustomerInDB);
    }
    
    @Test
    public void updateCustomerInDB() throws SQLException
    {
        Customer testCustomerInDB = customerDAO.insert(testCustomer);
        
        Customer updatedTestCustomer = new Customer(testCustomerInDB.getId(), "Mike", "Smith");
        
        Customer updatedTestCustomerInDB = customerDAO.update(updatedTestCustomer);
        
        assertEquals(updatedTestCustomer.getId(), updatedTestCustomerInDB.getId());
        
        assertTrue(updatedTestCustomer.equals(updatedTestCustomerInDB));
        assertNotEquals(testCustomerInDB, updatedTestCustomerInDB);
        
        ResultSet results = executePreparedStatement(StatementType.QUERY, updatedTestCustomer);
        
        assertTrue(results.next());
        
        assertNotEquals(new Customer(results.getInt(1), results.getString(2), results.getString(3)), testCustomerInDB);
        assertEquals(new Customer(results.getInt(1), results.getString(2), results.getString(3)), updatedTestCustomerInDB);
    }
    
    @Test
    public void deleteCustomerFromDB() throws SQLException
    {   
        testCustomer = new Customer(0, "John", "Doe");
        
        ResultSet results = executePreparedStatement(StatementType.QUERY, testCustomer);
        
        assertFalse(results.next());
        assertFalse(customerDAO.delete(testCustomer));
        
        Customer testCustomerInDB = customerDAO.insert(testCustomer);
        results = executePreparedStatement(StatementType.QUERY, testCustomerInDB);
        
        assertTrue(results.next());      
        assertTrue(customerDAO.delete(testCustomerInDB));
        
        results = executePreparedStatement(StatementType.QUERY, testCustomerInDB);
        assertFalse(results.next());
    }
    
    @Test
    public void readSingleCustomerFromDB() throws SQLException
    {
        testCustomer = new Customer(0, "John", "Doe");
        
        assertEquals(null, customerDAO.read(testCustomer.getId()));
        
        Customer testCustomerInDB = customerDAO.insert(testCustomer);
        assertEquals(testCustomerInDB, customerDAO.read(testCustomerInDB.getId()));
        
        ResultSet results = executePreparedStatement(StatementType.QUERY, testCustomerInDB);
        
        assertTrue(results.next());
        
        assertEquals(new Customer(results.getInt(1), results.getString(2), results.getString(3)), customerDAO.read(testCustomerInDB.getId()));
        assertEquals(new Customer(results.getInt(1), results.getString(2), results.getString(3)), customerDAO.read(results.getInt(1)));
        
        
    }
    
    public ResultSet executePreparedStatement(StatementType type, Customer customer) throws SQLException
    {
        PreparedStatement statement;
        ResultSet results;
        
        switch(type)
        {
        case QUERY:
            statement = this.connection.prepareStatement("SELECT * FROM customers WHERE id = ?");
            statement.setInt(1, customer.getId());
            results = statement.executeQuery();
            break;
        case DELETE:
            statement = this.connection.prepareStatement("DELETE FROM customers WHERE id = ?",Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, customer.getId());
            statement.executeUpdate();
            results = statement.getGeneratedKeys();
            break;
        case INSERT:
            statement = this.connection.prepareStatement("SELECT * FROM customers WHERE id = ?");
            statement.setInt(1, customer.getId());
            results = statement.executeQuery();
            if(!results.next())
            {
                statement = this.connection.prepareStatement("INSERT INTO customers (first, last) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, customer.getFirstName());
                statement.setString(2, customer.getLastName());
                results = statement.getGeneratedKeys();
            }
            else
            {
                throw new IllegalArgumentException("Customer already exists in database");
            }
            break;
        case UPDATE:
            statement = this.connection.prepareStatement("SELECT * FROM customers WHERE id = ?");
            statement.setInt(1, customer.getId());
            results = statement.executeQuery();
            if(results.next())
            {
                statement = this.connection.prepareStatement("UPDATE customers SET first = ?, last = ? WHERE id = ?", Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, customer.getFirstName());
                statement.setString(2, customer.getLastName());
                statement.setInt(3, customer.getId());
                results = statement.getGeneratedKeys();
            }
            else
            {
                throw new IllegalArgumentException("Customer does not exist in database");
            }
            break;
        default:
            results = null;
            break;
        }
        
        return results;
    }
    
    public enum StatementType
    {
        QUERY, INSERT, UPDATE, DELETE;
    }

}
