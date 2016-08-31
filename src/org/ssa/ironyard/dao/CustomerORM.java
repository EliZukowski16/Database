package org.ssa.ironyard.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.ssa.ironyard.model.Customer;

public interface CustomerORM extends ORM<Customer>
{
    default String projection()
    {
        return " id, first, last ";
    }

    default String table()
    {
        return " customers ";
    }

    default Customer map(ResultSet results) throws SQLException
    {
        return new Customer(results.getInt("id"), results.getString("first"), results.getString("last"));
    }

    default String prepareInsert()
    {
        return " INSERT INTO " + table() + " (first, last) VALUES (?,?) ";
    }

    default String prepareUpdate()
    {
        return " UPDATE " + table() + " SET first = ?, last = ? WHERE id = ? ";
    }

//    default String prepareDelete()
//    {
//        return " DELETE FROM " + table() + " WHERE id = ? ";
//    }
//
//    default String prepareRead()
//    {
//        return " SELECT " + projection() + " FROM " + table() + "WHERE id = ? ";
//    }
}
