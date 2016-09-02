package org.ssa.ironyard.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.ssa.ironyard.model.DomainObject;
import org.ssa.ironyard.orm.ORM;

public abstract class AbstractDAO<T extends DomainObject> implements DAO<T>
{
    protected final DataSource datasource;
    protected final ORM<T> orm;

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
            if (delete.executeUpdate() > 0)
                return true;
        }
        catch (Exception ex)
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

    public void clear()
    {
        Statement deleteAllData = null;
        Connection connection = null;
        try
        {
            connection = datasource.getConnection();
            deleteAllData = connection.createStatement();
            deleteAllData.execute("DELETE FROM " + this.orm.table());
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
