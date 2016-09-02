package org.ssa.ironyard.model;

public abstract class AbstractDomainObject
{
    protected final Integer id;
    protected Boolean loaded;
    
    protected AbstractDomainObject(Integer id)
    {
        this(id, false);
    }
    
    protected AbstractDomainObject(Integer id, boolean loaded)
    {
        this.id = id;
        this.loaded = loaded;
    }
    
    public void setLoaded(boolean loaded)
    {
        this.loaded = loaded;
    }
    
    public Integer getId()
    {
        return id;
    }
    
    public boolean isLoaded()
    {
        return loaded;
    }
    
//    public abstract DomainObject clone();
    
}
