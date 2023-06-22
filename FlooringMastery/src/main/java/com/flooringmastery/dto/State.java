package com.flooringmastery.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class State 
{

    private String stateAbbreviation;
    private String stateName;
    private BigDecimal taxRate;

    public State() 
    {
    	
    }

    public State(String stateAbbreviation, String stateName, BigDecimal taxRate) 
    {
        this.stateAbbreviation = stateAbbreviation;
        this.stateName = stateName;
        this.taxRate = taxRate;
    }

    public String getStateAbbreviation() 
    {
        return stateAbbreviation;
    }

    public void setStateAbbreviation(String stateAbbreviation) 
    {
        this.stateAbbreviation = stateAbbreviation;
    }

    public String getStateName() 
    {
        return stateName;
    }

    public void setStateName(String stateName) 
    {
        this.stateName = stateName;
    }

    public BigDecimal getTaxRate() 
    {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) 
    {
        this.taxRate = taxRate;
    }

    @Override
    public boolean equals(Object o) 
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return Objects.equals(stateAbbreviation, state.stateAbbreviation) && Objects.equals(stateName, state.stateName) && Objects.equals(taxRate, state.taxRate);
    }

    @Override
    public int hashCode() 
    {
        return Objects.hash(stateAbbreviation, stateName, taxRate);
    }

    @Override
    public String toString() 
    {
        return "State{" +
                "stateAbbreviation='" + stateAbbreviation + '\'' +
                ", stateName='" + stateName + '\'' +
                ", taxRate=" + taxRate +
                '}';
    }
}