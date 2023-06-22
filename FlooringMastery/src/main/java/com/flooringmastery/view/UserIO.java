package com.flooringmastery.view;

import java.math.BigDecimal;

public interface UserIO 
{

    void print(String message);

    String readString(String prompt);

    String readStringNoEmpty(String prompt);

    String readNames(String prompt);

    String readNamesAllowEmpty(String prompt);

    int readInt(String prompt);

    int readInt(String prompt, int min, int max);

    int readIntAllowEmpty(String prompt, int min, int max);

    BigDecimal readBigDecimal(String prompt, BigDecimal min, BigDecimal max);

    BigDecimal readBigDecimalAllowEmpty(String prompt, BigDecimal min, BigDecimal max);
}