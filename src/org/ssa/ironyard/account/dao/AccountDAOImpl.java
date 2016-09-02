package org.ssa.ironyard.account.dao;

import javax.sql.DataSource;

import org.ssa.ironyard.account.orm.AccountORMImpl;

public class AccountDAOImpl extends AbstractAccountDAO implements AccountDAO
{
    
    protected AccountDAOImpl(DataSource datasource)
    {
        super(datasource, new AccountORMImpl());
    }

    // static final Logger LOGGER = LogManager.getLogger(AccountDAOImpl.class);

}