package com.flooringmastery.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.flooringmastery.dao.exceptions.FlooringMasteryPersistenceException;
import com.flooringmastery.dao.exceptions.NoOrdersOnDateException;
import com.flooringmastery.dto.Action;
import com.flooringmastery.dto.Order;
import com.flooringmastery.dto.Product;
import com.flooringmastery.dto.State;
import com.flooringmastery.service.FlooringMasteryService;
import com.flooringmastery.service.exception.InvalidDateException;
import com.flooringmastery.service.exception.InvalidStateException;
import com.flooringmastery.service.exception.NoSuchOrderException;
import com.flooringmastery.service.exception.NoSuchProductException;
import com.flooringmastery.view.FlooringMasteryView;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Component
public class FlooringMasteryController 
{

    @Autowired
    FlooringMasteryService service;
    @Autowired
    FlooringMasteryView view;

    public void run() throws InterruptedException 
    {
        boolean isInitialized = false;
        try 
        {
            initializeProgram();
            view.displayWelcomeBanner();
            isInitialized = true;
        } 
        
        catch (FlooringMasteryPersistenceException e) 
        {
            view.displayErrorMessage(e.getMessage());
        }

        int mainMenuSelection = 0;
        while (isInitialized) 
        {
            view.printMainMenu();
            mainMenuSelection = view.retrieveMainMenuSelection();

            try 
            {
                switch (mainMenuSelection) 
                {
                    case 1:
                        displayOrders();
                        break;
                    case 2:
                        addOrder();
                        break;
                    case 3:
                        editOrder();
                        break;
                    case 4:
                        removeOrder();
                        break;
                    case 5:
                        exportData();
                        break;
                    case 6:
                        isInitialized = false;
                        break;
                }
            } 
            catch (NoOrdersOnDateException | InvalidDateException | NoSuchOrderException | InvalidStateException |
                     NoSuchProductException e) 
            {
                view.displayErrorMessage(e.getMessage());
            } 
            catch (FlooringMasteryPersistenceException | IOException e) 
            {
                view.displayErrorMessage(e.getMessage());
                isInitialized = false;
            }
        }
        view.displayExitBanner();
    }

    private void initializeProgram() throws FlooringMasteryPersistenceException 
    {
        service.loadStatesAndProducts();
    }

    private void displayOrders() throws FlooringMasteryPersistenceException, NoOrdersOnDateException 
    {
        LocalDate orderDate = view.retrieveOrderDate();
        service.setOrdersFile(orderDate, Action.DISPLAY);
        List<Order> ordersList = service.retrieveOrdersList(Action.DISPLAY); // throws exception if no orders found for date
        view.displayViewAllOrdersBanner(orderDate);
        view.displayOrders(ordersList);
    }

    private void addOrder() throws InvalidDateException, FlooringMasteryPersistenceException, NoOrdersOnDateException,
            IOException, InvalidStateException, NoSuchProductException 
    {
        view.displayAddOrderBanner();
        LocalDate orderDate = view.retrieveOrderDate();

        // Order date must be in the future when adding new orders, will throw Exception if it is not.
        service.validateDate(orderDate);
        // Assigning order to correct file, creates an Orders file for the specified date if one does not exist.
        service.setOrdersFile(orderDate, Action.ADD);

        Order newOrder = new Order();
        // Setting order's order number
        newOrder.setOrderNumber(service.generateOrderNumber());

        // Setting order's customer name, state, tax rate, product type, area, cost per sq. ft., and labor cost per sq. ft.
        List<State> statesList = service.retrieveStatesList();
        List<Product> productsList = service.retrieveProductsList();
        newOrder = view.retrieveOrderInformation(statesList, productsList, Action.ADD, newOrder);

        // Validates state and product type entered by user.
        service.validateState(newOrder.getState());
        service.validateProduct(newOrder.getProductType());

        // Calculates order's material cost, labor cost, tax, and total.
        service.calculatePrices(newOrder);

        // Confirming user order.
        boolean informationIsConfirmed = view.confirmAction(newOrder, Action.ADD);
        if (informationIsConfirmed) 
        {
            service.enterOrder(newOrder, orderDate);
            view.displayAddOrderSuccessBanner(newOrder);
        } 
        else 
        {
            // If ordersList size is 0 that means this was going to be the first order added to the loaded Orders file, so now
            // that the order is aborted, we must delete that file, otherwise we would leave an empty Orders file in the program.
            List<Order> ordersList = service.retrieveOrdersList(Action.ADD);
            
            if (ordersList.size() == 0) 
            {
                service.deleteEmptyFile();
            }
            view.displayOrderCanceledBanner();
        }
    }

    private void editOrder() throws NoOrdersOnDateException, FlooringMasteryPersistenceException, NoSuchOrderException,
            InvalidStateException, NoSuchProductException 
    {
        view.displayEditOrderBanner();
        LocalDate date = view.retrieveOrderDate();
        service.setOrdersFile(date, Action.EDIT); // will throw exception if Orders file does not exist for specified date

        service.retrieveOrdersList(Action.EDIT); // will throw exception if no orders for that date

        int orderNumber = view.retrieveOrderNumber(Action.EDIT);
        Order orderToEdit = service.retrieveOrder(orderNumber); // will throw exception if no order is found with that number

        // Creating an order with orderToEdit's information before it's edited, so we can compare and see if order was edited.
        Order orderToCompare = new Order(orderToEdit.getOrderNumber(), orderToEdit.getCustomerName(),
                orderToEdit.getState(), orderToEdit.getTaxRate(), orderToEdit.getProductType(), orderToEdit.getArea(),
                orderToEdit.getCostPerSquareFoot(), orderToEdit.getLaborCostPerSquareFoot(), orderToEdit.getMaterialCost(),
                orderToEdit.getLaborCost(), orderToEdit.getTax(), orderToEdit.getTotal());
        List<State> statesList = service.retrieveStatesList();
        List<Product> productsList = service.retrieveProductsList();
        orderToEdit = view.retrieveOrderInformation(statesList, productsList, Action.EDIT, orderToEdit);

        // Validates state and product entered by user.
        service.validateState(orderToEdit.getState());
        service.validateProduct(orderToEdit.getProductType());

        if (orderToEdit.equals(orderToCompare)) 
        {
            view.displayNoEditDoneMessage();
        }
        else 
        {
            // Calculates order's material cost, labor cost, tax, and total.
            service.calculatePrices(orderToEdit);

            boolean informationIsConfirmed = view.confirmAction(orderToEdit, Action.EDIT);
            if (informationIsConfirmed) 
            {
                service.storeEditedOrder(orderToEdit, date);
                view.displayEditOrderSuccessBanner();
            } 
            else 
            {
                view.displayCancelEditBanner();
            }
        }
    }

    private void removeOrder() throws FlooringMasteryPersistenceException, NoOrdersOnDateException, NoSuchOrderException 
    {
        view.displayRemoveOrderBanner();
        LocalDate dateChoice = view.retrieveOrderDate();
        service.setOrdersFile(dateChoice, Action.REMOVE); // will throw exception if Orders file does not exist for specified date

        List<Order> ordersList = service.retrieveOrdersList(Action.REMOVE); // will throw exception if no orders for that date

        int orderNumber = view.retrieveOrderNumber(Action.REMOVE);
        Order orderToRemove = service.retrieveOrder(orderNumber);

        boolean deletionIsConfirmed = view.confirmAction(orderToRemove, Action.REMOVE);

        if (deletionIsConfirmed) 
        {
            service.removeOrder(orderToRemove, dateChoice);
            // If ordersList size is 0 after order removal, we've removed all orders for that date and can delete the
            // loaded Orders file.
            if (ordersList.size() == 0) 
            {
                service.deleteEmptyFile();
            }
            view.displayRemoveOrderSuccessBanner();
        } 
        else 
        {
            view.displayCancelRemoveBanner();
        }
    }

    private void exportData() throws FlooringMasteryPersistenceException, NoOrdersOnDateException 
    {
        service.exportData();
        view.displayExportDataSuccessBanner();
    }
}
