package com.flooringmastery.dao;


import com.flooringmastery.dao.exceptions.FlooringMasteryPersistenceException;

public interface FlooringMasteryAuditDao 
{
    void writeAuditEntry(String message, String fileName) throws FlooringMasteryPersistenceException;
}