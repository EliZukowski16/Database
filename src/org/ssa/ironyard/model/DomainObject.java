package org.ssa.ironyard.model;

public interface DomainObject extends Cloneable
{
    Integer getId();

    DomainObject clone();
}
