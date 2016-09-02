package org.ssa.ironyard.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public interface DAO<T>
{
    public T insert(T domain);

    public boolean delete(int id);

    public T update(T domain);

    public T read(int id);

    default void cleanup(ResultSet results, Statement statement, Connection connection)
    {
        try
        {
            if (results != null)
                results.close();
            cleanup(statement, connection);
        }
        catch (Exception ex)
        {

        }
    }

    default void cleanup(Statement statement, Connection connection)
    {
        try
        {
            if (statement != null)
                statement.close();
            if (connection != null)
                connection.close();
        }
        catch (Exception ex)
        {

        }
    }

    public void clear();

}
