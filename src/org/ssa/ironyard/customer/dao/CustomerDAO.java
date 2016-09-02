package org.ssa.ironyard.customer.dao;

import java.util.List;

import org.ssa.ironyard.customer.model.Customer;
import org.ssa.ironyard.dao.DAO;

public interface CustomerDAO extends DAO<Customer>
{
    public List<Customer> read();
    public List<Customer> readFirstName(String firstName);
    public List<Customer> readLastName(String lastName);
}
