package org.ssa.ironyard.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

public abstract class AbstractDAO<T extends DomainObject> 
{
    final DataSource datasource;
    final ORM<T> orm;

    protected AbstractDAO(DataSource datasource, ORM<T> orm)
    {
        this.datasource = datasource;
        this.orm = orm;
    }

    public abstract T insert(T domain);
    public boolean delete(int id)
    {
        Connection connection = null;
        PreparedStatement delete = null;
        
        try
        {
            connection = this.datasource.getConnection();
            delete = connection.prepareStatement(this.orm.prepareDelete());
            delete.setInt(1, id);
            if(delete.executeUpdate() > 0)
                return true;
        }
        catch(Exception ex)
        {
            
        }
        finally
        {
            cleanup(delete, connection);
        }
        return false;
    }
    public abstract T update(T domain);
    public T read(int id)
    {
        Connection connection = null;
        PreparedStatement read = null;
        ResultSet query = null;
        
        try
        {
            connection = this.datasource.getConnection();
            read = connection.prepareStatement(this.orm.prepareRead());
            read.setInt(1, id);
            query = read.executeQuery();
            if (query.next())
                return this.orm.map(query);

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
    static protected void cleanup(ResultSet results, Statement statement, Connection connection)
    {
        try
        {
            if(results != null)
                results.close();
            cleanup(statement, connection);
        }
        catch(Exception ex)
        {
            
        }
    }
    
    static protected void cleanup(Statement statement, Connection connection)
    {
        try
        {
            if(statement != null)
                statement.close();
            if(connection != null)
                connection.close();
        }
        catch(Exception ex)
        {
            
        }
    }
}
