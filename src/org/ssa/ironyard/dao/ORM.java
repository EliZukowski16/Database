package org.ssa.ironyard.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ORM<T extends DomainObject>
{
    String projection();
    String table();
    T map(ResultSet results) throws SQLException;
    String prepareInsert();
    String prepareUpdate();
    default String prepareRead()
    {
        return " SELECT " + projection() + " FROM " + table() + "WHERE id = ? ";
    }
    default String prepareDelete()
    {
        return " DELETE FROM " + table() + " WHERE id = ? ";
    }
}
