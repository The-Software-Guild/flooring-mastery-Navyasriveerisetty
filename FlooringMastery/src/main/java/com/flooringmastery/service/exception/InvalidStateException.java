package com.flooringmastery.service.exception;

public class InvalidStateException extends Exception 
{

    public InvalidStateException(String message) 
    {
        super(message);
    }

    public InvalidStateException(String message, Throwable cause) 
    {
        super(message, cause);
    }
}