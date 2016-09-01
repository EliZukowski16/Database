package org.ssa.ironyard.model;

import java.math.BigDecimal;

public class Account implements DomainObject
{
    private final Integer id;
    private AccountType type;
    private BigDecimal balance;
    private Customer customer;

    public enum AccountType
    {
        SAVINGS("SA"), CHECKING("CH");

        private String accountType;

        private AccountType(String accountType)
        {
            this.accountType = accountType;
        }

        public String getAccountType()
        {
            return accountType;
        }

        public static AccountType getInstance(String accountType)
        {
            for(AccountType t: values())
            {
                if(t.accountType.equals(accountType))
                    return t;
            }
            return null;
        }
    }

    public Account(Customer customer, AccountType type, BigDecimal balance)
    {
        this.id = null;
        this.customer = customer;
        this.type = type;
        this.balance = balance;
    }

    public Account(Integer id, Customer customer, AccountType type, BigDecimal balance)
    {
        this.id = id;
        this.customer = customer;
        this.type = type;
        this.balance = balance;
    }

    public Account()
    {
        this.id = null;
        this.customer = new Customer();
        this.type = null;
        this.balance = null;
    }

    public Integer getId()
    {
        return id;
    }

    public Customer getCustomer()
    {
        return customer;
    }
    
    private void setCustomer(Customer customer)
    {
        this.customer = customer;
        
    }

    public AccountType getType()
    {
        return type;
    }
    
    public void setType(AccountType type)
    {
        this.type = type;
    }

    public BigDecimal getBalance()
    {
        return balance;
    }
    
    public void setBalance(BigDecimal balance)
    {
        this.balance = balance;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((balance == null) ? 0 : balance.hashCode());
        result = prime * result + ((customer == null) ? 0 : customer.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Account other = (Account) obj;
        if (id == null)
        {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        return true;
    }
    
    public boolean deeplyEquals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Account other = (Account) obj;
        if (balance == null)
        {
            if (other.balance != null)
                return false;
        }
        else if (balance.compareTo(other.balance) != 0)
            return false;
        if (customer == null)
        {
            if (other.customer != null)
                return false;
        }
        else if (!customer.equals(other.customer))
            return false;
        if (id == null)
        {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public Account clone()
    {
        try
        {
            Account copy = (Account) super.clone();
            copy.setCustomer(this.customer.clone());
            return copy;
        }
        catch(CloneNotSupportedException ex)
        {
            return null;
        }
    }



    
    
}
