package org.ssa.ironyard.account.dao;

import java.util.List;

import org.ssa.ironyard.account.model.Account;
import org.ssa.ironyard.dao.DAO;

public interface AccountDAO extends DAO<Account>
{
    public List<Account> readUser(int user);
    public List<Account> readUnderwater();
}
