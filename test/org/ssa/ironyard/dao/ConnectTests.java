package org.ssa.ironyard.dao;

import static org.junit.Assert.assertEquals;
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

public class ConnectTests
{
    static String URL = "jdbc:mysql://localhost/ssa_bank?user=root&password=root";

    DataSource datasource;
    Connection connection;

    @Before
    public void setupDB() throws SQLException
    {
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setUrl(URL);
        Connection connection = mysqlDataSource.getConnection();

        this.datasource = mysqlDataSource;
        this.connection = connection;
    }

    @After
    public void teardown() throws SQLException
    {
        this.connection.close();
    }

    @Test
    public void datasource() throws SQLException
    {
        Statement sql = connection.createStatement();

        ResultSet results = sql.executeQuery("SELECT * from customers WHERE id = 1");

        assertTrue(results.next());

        assertEquals(1, results.getInt(1));
        assertEquals("john", results.getString(2));
        assertEquals("doe", results.getString(3));
    }

    @Test
    public void prepare() throws SQLException
    {
        PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * from customers WHERE id = ?");
        preparedStatement.setInt(1, 1);
        ResultSet results = preparedStatement.executeQuery();

        assertTrue(results.next());

        assertEquals(1, results.getInt(1));
        assertEquals("john", results.getString(2));
        assertEquals("doe", results.getString(3));

    }

    // @Test
     public void create() throws SQLException
     {
     PreparedStatement preparedStatement = this.connection
     .prepareStatement("INSERT INTO customers (first, last) VALUES (?,?)");
    
     preparedStatement.setString(1, "jane");
     preparedStatement.setString(2, "doe");
     preparedStatement.execute();
    
     }

//    @Test
    public void insertAndDelete() throws SQLException
    {
        PreparedStatement statement = this.connection
                .prepareStatement("INSERT INTO customers (first, last) VALUES(?,?)", Statement.RETURN_GENERATED_KEYS);
        
        statement.setString(1, "mike");
        statement.setString(2, "smith");
        
        assertEquals(1, statement.executeUpdate());
        
        ResultSet results = statement.getGeneratedKeys();
        
        assertTrue(results.next());
        
        System.err.println("Inserted customer with id " + results.getInt(1));
        
        statement = this.connection.prepareStatement("DELETE FROM customers WHERE first = ?");
        statement.setString(1, "mike");
        
        assertEquals(1, statement.executeUpdate());
    }
    
    @Test
    public void sqlCommands() throws SQLException
    {
        String firstName;
        String lastName;
        Integer id;
        
        PreparedStatement statement = this.connection
                .prepareStatement("INSERT INTO customers (first, last) VALUES(?,?)", Statement.RETURN_GENERATED_KEYS);
        
        statement.setString(1, "bob");
        statement.setString(2, "thompson");
        
        assertEquals(1, statement.executeUpdate());
        
        ResultSet results = statement.getGeneratedKeys();
        
        assertTrue(results.next());
        id = results.getInt(1);
        System.err.println("Inserted customer with id " + id);
        
        statement = this.connection.prepareStatement("UPDATE customers SET first = ?, last = ? WHERE id = ?", Statement.RETURN_GENERATED_KEYS);
        
        statement.setString(1, "jane");
        statement.setString(2, "doe");
        statement.setInt(3, id);
        
        assertEquals(1, statement.executeUpdate());
        
        System.err.println("Update customer with id " + results.getInt(1));
        
        statement = this.connection.prepareStatement("SELECT * FROM customers WHERE id = ?");
        statement.setInt(1, results.getInt(1));
        
        results = statement.executeQuery();
        
        assertTrue(results.next());
        id = results.getInt(1);
        firstName = results.getString(2);
        lastName = results.getString(3);
        
        
        System.err.println("Customer id: " + id + " First Name: " + firstName + " Last Name: " + lastName);
        
        statement = this.connection.prepareStatement("DELETE FROM customers WHERE first = ?");
        statement.setString(1, "jane");
        
        assertEquals(1, statement.executeUpdate());
        
    }

}
