package org.ssa.ironyard.customer.dao;

import javax.sql.DataSource;

import org.ssa.ironyard.customer.orm.CustomerORMImpl;

public class CustomerDAOImpl extends AbstractCustomerDAO implements CustomerDAO
{

    public CustomerDAOImpl(DataSource datasource)
    {
        super(datasource, new CustomerORMImpl());
    }

}
