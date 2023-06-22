package com.flooringmastery.service;


import java.time.LocalDate;
import java.util.List;

import com.flooringmastery.dao.exceptions.FlooringMasteryPersistenceException;
import com.flooringmastery.dao.exceptions.NoOrdersOnDateException;
import com.flooringmastery.dto.Action;
import com.flooringmastery.dto.Order;
import com.flooringmastery.dto.Product;
import com.flooringmastery.dto.State;
import com.flooringmastery.service.exception.InvalidDateException;
import com.flooringmastery.service.exception.InvalidStateException;
import com.flooringmastery.service.exception.NoSuchOrderException;
import com.flooringmastery.service.exception.NoSuchProductException;

public interface FlooringMasteryService 
{

    /**
     * Calculates the material and labor costs, as well as the tax and total of the order.
     *
     * @param order the order we are calculating prices for
     * @return the order with all price properties added to it
     */
    Order calculatePrices(Order order);

    /**
     * Sets the name of the Orders file we should use depending on the date passed in by the user.
     *
     * @param date   the date passed in by the user
     * @param action the action being taken by the user (DISPLAY, ADD, EDIT, or REMOVE); depending on this, the DAO will create
     *               a new Orders file or not
     * @throws FlooringMasteryPersistenceException if the DAO has problems reading the current Orders file
     * @throws NoOrdersOnDateException             if the user is trying to access Orders on a date there aren't any
     */
    void setOrdersFile(LocalDate date, Action action) throws FlooringMasteryPersistenceException, NoOrdersOnDateException;

    /**
     * Validates that the user's chosen date is a date in the future when adding orders.
     *
     * @param date the date specified by the user
     */
    void validateDate(LocalDate date) throws InvalidDateException;

    /**
     * Generates an order number for the new order the user is adding.
     *
     * @return the order number
     */
    int generateOrderNumber();

    /**
     * Validates that the State chosen by the user is an available state. Even though we get the State from the user by
     * presenting a List of available states in the view, and the user can only choose from those, we still implement this
     * method that validates the state input to show that we must always protect our business logic from future changes, i.e.:
     * a change in the implementation of the view.
     *
     * @param stateAbbreviation the abbreviation of the state selected by the user
     * @throws InvalidStateException if the state entered by the user is not on the list of available states
     */
    void validateState(String stateAbbreviation) throws InvalidStateException;

    /**
     * Validates that the Product chosen by the user is an available product. Even though we get the Product from the user by
     * presenting a List of available products in the view, and the user can only choose from those, we still implement this
     * method that validates the state input to show that we must always protect our business logic from future changes, i.e.:
     * a change in the implementation of the view.
     *
     * @param productType the product type selected by the user
     * @throws NoSuchProductException if the product entered by the user is not on the list of available products
     */
    void validateProduct(String productType) throws NoSuchProductException;

    /**
     * Populates the statesList and productsList with the data in the Taxes and Products files respectively.
     *
     * @throws FlooringMasteryPersistenceException if the DAO has problems reading the Taxes or Products files
     */
    void loadStatesAndProducts() throws FlooringMasteryPersistenceException;

    /**
     * Retrieves a List of orders that was populated using the Orders file for the date the user specified.
     *
     * @param action the action the user is taking
     * @return the list of Orders for the specified date
     * @throws NoOrdersOnDateException if there are no orders for the specified date (the order's list size is 0) and the
     *                                 action being taken is not ADDING; if the action is ADDING, we do not throw an exception
     *                                 even if there are no orders for the specified date, as the user should then be able to
     *                                 add new orders for that date
     */
    List<Order> retrieveOrdersList(Action action) throws NoOrdersOnDateException;

    /**
     * Retrieves a List with State data that was populated using the Taxes file.
     *
     * @return the list of states
     */
    List<State> retrieveStatesList();

    /**
     * Retrieves a List with product data that was populated using the Products file.
     *
     * @return the list of products
     */
    List<Product> retrieveProductsList();

    /**
     * Retrieves an order with the specified order number.
     *
     * @param orderNumber the order number of the order we want to retrieve
     * @return the order with the specified order number
     * @throws NoSuchOrderException if no order is found with the specified order number
     */
    Order retrieveOrder(int orderNumber) throws NoSuchOrderException;

    /**
     * Writes new order to the corresponding Orders file and writes an audit entry of the operation.
     *
     * @param newOrder the new order to be added to the Orders file
     * @param date     the date the order is being placed
     * @throws FlooringMasteryPersistenceException if the DAO or auditDAO have problems writing to the Orders or Audit files.
     */
    void enterOrder(Order newOrder, LocalDate date) throws FlooringMasteryPersistenceException;

    /**
     * Writes edited order to the corresponding Orders file and writes an audit entry of the operation.
     *
     * @param order the edited order
     * @param date  the date of the edited order
     * @throws FlooringMasteryPersistenceException if the DAO has problems writing to the current Orders file
     */
    void storeEditedOrder(Order order, LocalDate date) throws FlooringMasteryPersistenceException;

    /**
     * Removes an order from the current Orders file and writes an audit entry of the operation.
     *
     * @param order the order to be removed
     * @param date  the date of the removed order
     * @throws FlooringMasteryPersistenceException if the DAO has problems writing to the current Orders file
     */
    void removeOrder(Order order, LocalDate date) throws FlooringMasteryPersistenceException;

    /**
     * Calls the DAO method that deletes the current Orders file if there are no orders in it.
     */
    void deleteEmptyFile();

    /**
     * Calls the DAO method that exports the orders from all Orders files to the DataExport file.
     *
     * @throws FlooringMasteryPersistenceException if the DAO has problems accessing an Orders file
     * @throws NoOrdersOnDateException             if the date the user is specifying does not have a corresponding Orders file
     *                                             (this should not ever happen in this method the way the program is
     *                                             structured, and is only needed because this method uses the
     *                                             setCurrentOrdersFile method, which can throw this exception)
     */
    void exportData() throws FlooringMasteryPersistenceException, NoOrdersOnDateException;
}