package com.flooringmastery.dao;


import org.springframework.stereotype.Component;

import com.flooringmastery.dao.exceptions.FlooringMasteryPersistenceException;
import com.flooringmastery.dao.exceptions.NoOrdersOnDateException;
import com.flooringmastery.dto.Action;
import com.flooringmastery.dto.Order;
import com.flooringmastery.dto.Product;
import com.flooringmastery.dto.State;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.List;



public interface FlooringMasteryDao 
{

    /**
     * Reads the Products and Taxes files when the program starts up to populate the productsList and statesLists.
     *
     * @throws FlooringMasteryPersistenceException if it cannot read the files
     */
    void loadStatesAndProductsLists() throws FlooringMasteryPersistenceException;

    /**
     * Adds a new order to the ordersList and writes the ordersList to the current Orders file.
     *
     * @param order the order to be added
     * @throws FlooringMasteryPersistenceException if there is a problem writing to the desired Orders file.
     */
    
    void addOrder(Order order) throws FlooringMasteryPersistenceException;

    /**
     * Writes the ordersList containing the edited order to the current Orders file.
     *
     * @throws FlooringMasteryPersistenceException if there is a problem writing to the desired Orders file.
     */
    
    void editOrder() throws FlooringMasteryPersistenceException;

    /**
     * Removes the order from the ordersList and writes the ordersList to the current Orders file.
     *
     * @param order order number to be associated with the order.
     * @throws FlooringMasteryPersistenceException if there is a problem writing to the desired Orders file.
     */
    
    void removeOrder(Order order) throws FlooringMasteryPersistenceException;

    /**
     * Clears ordersList and loads orders from currently selected Orders file into ordersList.
     *
     * @throws FlooringMasteryPersistenceException if there is a problem reading from the desired Orders file.
     */
    void loadOrdersFile() throws FlooringMasteryPersistenceException;

    /**
     * Deleted the currently loaded Orders file.
     * We only run this in cases where the file is empty, like when the user began adding the first order for a specific date,
     * but then decided to abort order placement. In this case if we do not delete the created Orders file we would be left
     * with an empty file for that date in the program.
     * We also run this method in case the user removed all orders for a specific date so we do not leave an empty orders file.
     */
    
    void deleteFile();

    /**
     * Returns a List of all Orders.
     *
     * @return Order List containing all orders from current Orders file.
     */
    
    List<Order> getOrdersList();

    /**
     * Returns a List of all States.
     *
     * @return State List containing all state data from the Taxes file.
     */
    
    List<State> getStatesList();

    /**
     * Returns a List of all Products.
     *
     * @return Product List containing all product data from the Products file.
     */
    List<Product> getProductsList();

    /**
     * Sets the correct name of file to use for the orders, as well as populate the ordersList with orders from that file, if
     * it contains any.
     * If the user is adding orders for a particular date, and that date does not exist, it creates a file for them. This will
     * only happen when the user is ADDING orders, not when DISPLAYING, EDITING or REMOVING orders.
     *
     * @param fileName the name of the file we want to use for the orders
     * @param action   the action the user is taking (displaying, adding, editing, or removing orders); we need this to know when
     *                 to create a new Orders file for a particular date
     * @throws FlooringMasteryPersistenceException if we cannot access the Orders file with the date the user is specifying
     * @throws NoOrdersOnDateException             if the date the user is specifying does not have a corresponding Orders file
     */
    void setCurrentOrdersFile(String fileName, Action action) throws FlooringMasteryPersistenceException,
            NoOrdersOnDateException;

    /**
     * Writes all active orders to the DataExport.txt file under backup folder.
     *
     * @throws FlooringMasteryPersistenceException if we cannot access the Orders file with the date the user is specifying
     * @throws NoOrdersOnDateException             if the date the user is specifying does not have a corresponding Orders file
     *                                             (this should not ever happen in this method the way the program is
     *                                             structured, and is only needed because this method uses the
     *                                             setCurrentOrdersFile method, which can throw this exception)
     */
    void writeToExportFile() throws FlooringMasteryPersistenceException, NoOrdersOnDateException;
}