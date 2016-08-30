package org.ssa.ironyard.database;

public interface CustomerDAO
{
    Customer insert(Customer customer);
    boolean delete(Customer toDelete);
    Customer update(Customer customer);
    Customer read(int id);
}
