package com.flooringmastery.view;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.flooringmastery.dto.Action;
import com.flooringmastery.dto.Order;
import com.flooringmastery.dto.Product;
import com.flooringmastery.dto.State;

@Component
public class FlooringMasteryView 
{

    @Autowired
    UserIO io;

    public void printMainMenu() 
    {
        io.print("\n* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
        io.print("* <<Flooring Program>>");
        io.print("* 1. Display Orders");
        io.print("* 2. Add an Order");
        io.print("* 3. Edit an Order");
        io.print("* 4. Remove an Order");
        io.print("* 5. Export All Data");
        io.print("* 6. Quit");
        io.print("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
    }

    public LocalDate retrieveOrderDate() 
    {
        while (true) 
        {
            try 
            {
                String dateString = io.readStringNoEmpty("\nEnter the order date (please input in MM/DD/YYYY format):");
                LocalDate orderDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                return orderDate;
            } 
            catch (DateTimeParseException e) 
            {
                io.print("The date entered was not in MM/DD/YYYY format, please try again.");
            }
        }
    }

    public void displayOrders(List<Order> orderList) 
    {

        for (Order order : orderList) 
        {
            io.print("Order Number: " + order.getOrderNumber());
            displayDivider();
            io.print("Customer Name: " + order.getCustomerName());
            io.print("State: " + order.getState());
            io.print("Product Type: " + order.getProductType());
            io.print("Area: " + order.getArea() + " sq. ft.");
            io.print("Material Cost: $" + order.getMaterialCost());
            io.print("Labor Cost: $" + order.getLaborCost());
            io.print("Tax: $" + order.getTax());
            io.print("Total: $" + order.getTotal() + "\n");
        }
        io.readString("Press enter to continue.");
    }

    public int retrieveOrderNumber(Action action) 
    {
        int orderNumber = 0;
        switch (action) {
            case EDIT:
                orderNumber = io.readInt("\nEnter the number of the order you wish to edit:");
                break;
            case REMOVE:
                orderNumber = io.readInt("\nEnter the number of the order you wish to remove:");
                break;
        }
        return orderNumber;
    }

    public Order retrieveOrderInformation(List<State> statesList, List<Product> productsList, Action action, Order order) 
    {
        int stateChoice; // will get index from statesList to select State
        int productChoice; // will get index from productsList to select Product

        switch (action) 
        {
            case ADD:
                // Getting customer name.
                String customerName = io.readNames("Enter customer name");

                // Getting state abbreviation and tax rate.
                io.print("Select a state from our list:");
                for (int i = 0; i < statesList.size(); i++) 
                {
                    io.print((i + 1) + " - " + statesList.get(i).getStateName());
                }
                stateChoice = io.readInt("Enter your choice:", 1, statesList.size());
                State state = statesList.get(stateChoice - 1); // -1 because list starts from 1 while index starts from 0
                String stateAbbreviation = state.getStateAbbreviation();
                BigDecimal taxRate = state.getTaxRate();

                // Getting product type, cost per square foot, and labor cost per square foot
                io.print("Select a product from our list:");
                for (int i = 0; i < productsList.size(); i++) 
                {
                    io.print((i + 1) + " - " + productsList.get(i).getProductType());
                }
                productChoice = io.readInt("Enter your choice:", 1, productsList.size());
                Product product = productsList.get(productChoice - 1); // -1 because list starts from 1 while index starts from 0
                String productType = product.getProductType();
                BigDecimal costPerSquareFoot = product.getCostPerSquareFoot();
                BigDecimal laborCostPerSquareFoot = product.getLaborCostPerSquareFoot();

                // Getting area
                BigDecimal area = io.readBigDecimal("Enter desired sq. ft. of product (minimum 100sq ft.):", new BigDecimal(
                        "100"), new BigDecimal("10000000"));

                order.setCustomerName(customerName);
                order.setState(stateAbbreviation);
                order.setTaxRate(taxRate);
                order.setProductType(productType);
                order.setArea(area);
                order.setCostPerSquareFoot(costPerSquareFoot);
                order.setLaborCostPerSquareFoot(laborCostPerSquareFoot);
                return order;
            case EDIT:
                // Changing customer name if user entered new customer name information.
                io.print("\nEnter new data to edit, or leave blank and press enter to leave intact:");
                String newCustomerName = io.readNamesAllowEmpty("Enter customer name (" + order.getCustomerName() + "):");
                if (!newCustomerName.equals("")) 
                {
                    order.setCustomerName(newCustomerName);
                }

                // Changing state abbreviation and tax rate if user entered new state information.
                io.print("Select a state from our list:");
                
                for (int i = 0; i < statesList.size(); i++) 
                {
                    io.print((i + 1) + " - " + statesList.get(i).getStateName());
                }
                
                stateChoice = io.readIntAllowEmpty("Enter state (" + order.getState() + "):", 1, statesList.size());
                
                if (stateChoice != 0) 
                { // If user changed state
                    state = statesList.get(stateChoice - 1); // -1 because list starts from 1 while index starts from 0
                    order.setState(state.getStateAbbreviation());
                    order.setTaxRate(state.getTaxRate());
                }

                // Changing product type, cost per sq. ft., and labor cost per sq. ft. if user entered new product information.
                io.print("Select a product from our list:");
                
                for (int i = 0; i < productsList.size(); i++) 
                {
                    io.print((i + 1) + " - " + productsList.get(i).getProductType());
                }
                productChoice = io.readIntAllowEmpty("Enter product type (" + order.getProductType() + "):", 1,
                        productsList.size());
                
                if (productChoice != 0) 
                {
                    product = productsList.get(productChoice - 1); // -1 because list starts from 1 while index starts from 0
                    order.setProductType(product.getProductType());
                    order.setCostPerSquareFoot(product.getCostPerSquareFoot());
                    order.setLaborCostPerSquareFoot(product.getLaborCostPerSquareFoot());
                }

                // Changing area if user entered new area information.
                BigDecimal newArea = io.readBigDecimalAllowEmpty("Enter area (" + order.getArea() + "):", new BigDecimal("100")
                        , new BigDecimal("10000000"));
                
                if (newArea != null) 
                {
                    order.setArea(newArea);
                }

                break;
        }
        return order;
    }

    public boolean confirmAction(Order order, Action action) 
    {
        io.print("\nOrder Review");
        io.print("\nOrder Number: " + order.getOrderNumber());
        io.print("Customer Name: " + order.getCustomerName());
        io.print("State: " + order.getState());
        io.print("Product Type: " + order.getProductType());
        io.print("Area: " + order.getArea() + " sq. ft.");
        io.print("Material Cost: $" + order.getMaterialCost());
        io.print("Labor Cost: $" + order.getLaborCost());
        io.print("Tax: $" + order.getTax());
        io.print("Total: $" + order.getTotal());

        int userChoice = 0;
        switch (action) 
        {
            case ADD:
                userChoice = io.readInt("\nConfirm order placement     1) YES     2) NO", 1, 2);
                break;
            case EDIT:
                userChoice = io.readInt("\nConfirm new order information:     1) YES     2) NO", 1, 2);
                break;
            case REMOVE:
                userChoice = io.readInt("\nConfirm order deletion:     1) YES     2) NO", 1, 2);
                break;
        }
        if (userChoice == 1) 
        {
            return true;
        } 
        else 
        {
            return false;
        }
    }

    public int retrieveMainMenuSelection()
    {
        return io.readInt("\nPlease choose an option from the menu: ", 1, 6);
    }

    public void displayAddOrderSuccessBanner(Order order)
    {
        io.print("\nOrder added successfully. Order number: " + order.getOrderNumber());
        io.readString("Press enter to continue.");
    }

    public void displayOrderCanceledBanner() 
    {
        io.print("\nOrder has been cancelled.");
        io.readString("Press enter to continue.");
    }

    public void displayEditOrderSuccessBanner() 
    {
        io.print("\nOrder edited successfully.");
        io.readString("Press enter to continue.");
    }

    public void displayCancelEditBanner() 
    {
        io.print("\nOrder editing cancelled.");
        io.readString("Press enter to continue.");
    }

    public void displayRemoveOrderSuccessBanner() 
    {
        io.print("\nOrder removed successfully.");
        io.readString("Press enter to continue.");
    }

    public void displayCancelRemoveBanner() 
    {
        io.print("\nOrder removal cancelled.");
        io.readString("Press enter to continue.");
    }

    public void displayErrorMessage(String errorMsg) throws InterruptedException 
    {
        io.print("\n" + errorMsg);
        Thread.sleep(1500);
    }

    public void displayNoSuchOrderMessage() 
    {
        io.print("\nNo order found with that order number for the selected date.");
        io.readString("Press enter to continue.");
    }

    public void displayNoEditDoneMessage() 
    {
        io.print("None of the order's information was changed.");
        io.readString("Press enter to continue.");
    }

    public void displayWelcomeBanner() 
    {
        io.print("\n~*~*~*~*~*~* WELCOME TO THE FLOORING MASTERY PROGRAM *~*~*~*~*~*~");
    }

    public void displayDivider() 
    {
        io.print("-------------------");
    }

    public void displayExitBanner() 
    {
        io.print("\nThank you for using the Flooring Mastery Program! Goodbye!!");
    }

    public void displayViewAllOrdersBanner(LocalDate currentDate) 
    {
        io.print("\n===== ORDERS FOR " + currentDate + " =====");
    }

    public void displayExportDataSuccessBanner() 
    {
        io.readString("All data was exported successfully. Please refer to DataExport.txt and hit any key to continue.");
    }

    public void displayAddOrderBanner() 
    {
        io.print("\n================ ADD ORDER ================");
    }

    public void displayEditOrderBanner() 
    {
        io.print("\n================ EDIT ORDER ================");
    }

    public void displayRemoveOrderBanner() 
    {
        io.print("\n================ REMOVE ORDER ================");
    }
}