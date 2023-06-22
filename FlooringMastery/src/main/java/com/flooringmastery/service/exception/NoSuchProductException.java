package com.flooringmastery.service.exception;

public class NoSuchProductException extends Exception 
{

    public NoSuchProductException(String message) 
    {
        super(message);
    }

    public NoSuchProductException(String message, Throwable cause) 
    {
        super(message, cause);
    }
}
