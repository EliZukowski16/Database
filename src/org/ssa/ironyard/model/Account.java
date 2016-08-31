package org.ssa.ironyard.model;

import org.ssa.ironyard.dao.DomainObject;

public class Account implements DomainObject
{
    private final Integer id;
    private final Integer customerId;
    private final AccountType type;
    private final Double balance;
//    private final Customer customer;

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
    }

    public Account(Integer customerId, AccountType type, Double balance)
    {
        this.id = null;
        this.customerId = customerId;
        this.type = type;
        this.balance = balance;
    }

    public Account(Integer id, Integer customerId, AccountType type, Double balance)
    {
        this.id = id;
        this.customerId = customerId;
        this.type = type;
        this.balance = balance;
    }

    public Account()
    {
        this.id = null;
        this.customerId = null;
        this.type = null;
        this.balance = null;
    }

    public Integer getId()
    {
        return id;
    }

    public Integer getCustomerId()
    {
        return customerId;
    }

    public AccountType getType()
    {
        return type;
    }

    public Double getBalance()
    {
        return balance;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((balance == null) ? 0 : balance.hashCode());
        result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
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
        else if (!balance.equals(other.balance))
            return false;
        if (customerId == null)
        {
            if (other.customerId != null)
                return false;
        }
        else if (!customerId.equals(other.customerId))
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
    
    

}
