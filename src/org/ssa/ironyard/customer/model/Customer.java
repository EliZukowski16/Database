package org.ssa.ironyard.customer.model;

import org.ssa.ironyard.model.AbstractDomainObject;
import org.ssa.ironyard.model.DomainObject;

public class Customer extends AbstractDomainObject implements DomainObject
{
    private String firstName;
    private String lastName;

    public Customer(Integer id, String firstName, String lastName, boolean loaded)
    {
        super(id, loaded);
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    public Customer(Integer id, String firstName, String lastName)
    {
        this(id, firstName, lastName, false);
    }

    public Customer(String firstName, String lastName)
    {
        this(null, firstName, lastName);
    }

    public Customer()
    {
        this("", "");
    }


    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + ((loaded == null) ? 0 : loaded.hashCode());
        return result;
    }

    @Override
    public boolean deeplyEquals(DomainObject obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Customer other = (Customer) obj;
        if (firstName == null)
        {
            if (other.firstName != null)
                return false;
        }
        else if (!firstName.equals(other.firstName))
            return false;
        if (id == null)
        {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        if (lastName == null)
        {
            if (other.lastName != null)
                return false;
        }
        else if (!lastName.equals(other.lastName))
            return false;
        if (loaded == null)
        {
            if (other.loaded != null)
                return false;
        }
        else if (!loaded.equals(other.loaded))
            return false;
        return true;
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
        Customer other = (Customer) obj;
        if (id == null)
        {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public Customer clone()
    {
        try
        {
            return (Customer) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
            return null;
        }
    }

}
