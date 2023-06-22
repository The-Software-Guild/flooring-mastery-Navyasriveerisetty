package com.flooringmastery.dao;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.stereotype.Component;

import com.flooringmastery.dao.exceptions.FlooringMasteryPersistenceException;
import com.flooringmastery.dao.exceptions.NoOrdersOnDateException;
import com.flooringmastery.dto.Action;
import com.flooringmastery.dto.Order;
import com.flooringmastery.dto.Product;
import com.flooringmastery.dto.State;

@Component
public class FlooringMasteryDaoImpl implements FlooringMasteryDao 
{

	    private final File STATE_FILE;
	    private final File PRODUCT_FILE;
	    private final File EXPORT_FILE;
	    private final String DELIMITER = ",";
	    private File currentOrdersFile; // not final, as this file changes
	    private List<Order> ordersList = new ArrayList<>();
	    private List<State> statesList = new ArrayList<>();
	    private List<Product> productsList = new ArrayList<>();

	    public FlooringMasteryDaoImpl() 
	    {
	        STATE_FILE = new File(".\\data\\Taxes.txt");
	        PRODUCT_FILE = new File(".\\data\\Products.txt");
	        EXPORT_FILE = new File(".\\backup\\DataExport.txt");
	    }

	    // This Constructor used for testing.
	    public FlooringMasteryDaoImpl(File testFile) 
	    {
	        currentOrdersFile = testFile;
	        STATE_FILE = new File(".\\data\\Taxes.txt");
	        PRODUCT_FILE = new File(".\\data\\Products.txt");
	        EXPORT_FILE = new File(".\\backup\\DataExport.txt");
	    }

	    @Override
	    public void loadStatesAndProductsLists() throws FlooringMasteryPersistenceException 
	    {
	        loadStateFile();
	        loadProductFile();
	    }

	    private void loadProductFile() throws FlooringMasteryPersistenceException 
	    {
	        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(PRODUCT_FILE)))) 
	        {
	            // Getting rid of the Products file header, so we do not add it to the productsList
	            scanner.nextLine();

	            while (scanner.hasNextLine()) 
	            {
	                String currentLine = scanner.nextLine();
	                Product currentProduct = unmarshallProduct(currentLine);
	                productsList.add(currentProduct);
	            }
	        } 
	        catch (FileNotFoundException e) 
	        {
	            throw new FlooringMasteryPersistenceException("Error. Could not load product data from the Products file, it may " +
	                    "have been moved or deleted.");
	        }
	    }

	    private void loadStateFile() throws FlooringMasteryPersistenceException 
	    {
	        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(STATE_FILE)))) 
	        {
	            // Getting rid of the State file header, so we do not add it to the statesList
	            scanner.nextLine();

	            while (scanner.hasNextLine()) 
	            {
	                String currentLine = scanner.nextLine();
	                State currentState = unmarshallState(currentLine);
	                statesList.add(currentState);
	            }
	        } 
	        catch (FileNotFoundException e) 
	        {
	            throw new FlooringMasteryPersistenceException("Error. Could not load state data from the Taxes file may have been " +
	                    "moved or deleted.");
	        }
	    }

	    @Override
	    public void setCurrentOrdersFile(String fileName, Action action) throws FlooringMasteryPersistenceException,
	            NoOrdersOnDateException 
	    {
	        currentOrdersFile = new File(fileName);

	        // If the user wants to add an order, and the Orders file for that date doesn't exist, creates it.
	        if (action == Action.ADD) 
	        {
	            if (!currentOrdersFile.exists()) 
	            {
	                createNewOrdersFile(currentOrdersFile);
	            }
	        } 
	        else if (!currentOrdersFile.exists()) 
	        {
	            throw new NoOrdersOnDateException("There are no orders for the specified date.");
	        }

	        loadOrdersFile();
	    }

	    private void createNewOrdersFile(File newOrdersFile) throws FlooringMasteryPersistenceException 
	    {
	        try 
	        {
	            newOrdersFile.createNewFile();
	        } 
	        catch (IOException e) 
	        {
	            throw new FlooringMasteryPersistenceException("Error creating new orders file.");
	        }
	    }

	    @Override
	    public void loadOrdersFile() throws FlooringMasteryPersistenceException 
	    {
	        // Clearing list, so it can be populated with current information as we changed Orders file or edited its contents.
	        ordersList.clear();

	        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(currentOrdersFile)))) 
	        {
	            // Getting rid of the Orders file header, so we do not add it to the ordersList
	            if (scanner.hasNextLine()) 
	            {
	                scanner.nextLine();
	            }

	            while (scanner.hasNextLine()) 
	            {
	                String currentLine = scanner.nextLine();
	                Order currentOrder = unmarshallOrder(currentLine);
	                ordersList.add(currentOrder);
	            }
	        }
	        
	        catch (FileNotFoundException e) 
	        {
	            throw new FlooringMasteryPersistenceException("Could not load file into memory.");
	        }
	    }

	    @Override
	    public void addOrder(Order newOrder) throws FlooringMasteryPersistenceException 
	    {
	        ordersList.add(newOrder);
	        writeOrdersFile(ordersList);
	    }

	    @Override
	    public void editOrder() throws FlooringMasteryPersistenceException 
	    {
	        writeOrdersFile(ordersList);
	    }

	    @Override
	    public void removeOrder(Order order) throws FlooringMasteryPersistenceException 
	    {
	        ordersList.remove(order);
	        writeOrdersFile(ordersList);
	    }

	    private void writeOrdersFile(List<Order> orderList) throws FlooringMasteryPersistenceException 
	    {
	        try (PrintWriter out = new PrintWriter(new FileWriter(currentOrdersFile))) 
	        {
	            String ordersFileHeader = "OrderNumber,CustomerName,State,TaxRate,ProductType,Area,CostPerSquareFoot," +
	                    "LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total";
	            out.println(ordersFileHeader);

	            orderList.forEach(order -> {
	                String orderAsText = marshallOrder(order);
	                out.println(orderAsText);
	                out.flush();
	            });
	        } 
	        catch (IOException e) 
	        {
	            throw new FlooringMasteryPersistenceException("Error. Could not write order data to file.");
	        }
	    }

	    @Override
	    public void deleteFile() 
	    {
	        currentOrdersFile.delete();
	    }

	    @Override
	    public List<Order> getOrdersList() 
	    {
	        return ordersList;
	    }

	    @Override
	    public List<State> getStatesList() 
	    {
	        return statesList;
	    }

	    @Override
	    public List<Product> getProductsList() 
	    {
	        return productsList;
	    }

	    private String getDateAsString(File orderFile) 
	    {
	        String[] fileTokens = orderFile.getName().split("_|\\.");
	        String unformattedDate = fileTokens[1];
	        LocalDate ld = LocalDate.parse(unformattedDate, DateTimeFormatter.ofPattern("MMddyyyy"));
	        return ld.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
	    }

	    public void writeToExportFile() throws FlooringMasteryPersistenceException, NoOrdersOnDateException 
	    {
	        try (PrintWriter out = new PrintWriter(new FileWriter(EXPORT_FILE))) 
	        {
	            String exportFileHeader = "OrderNumber,CustomerName,State,TaxRate,ProductType,Area,CostPerSquareFoot," +
	                    "LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total,Date";
	            out.println(exportFileHeader);

	            //collects files from the orders folder
	            File folder = new File(".\\orders\\");
	            File[] orderFiles = folder.listFiles();

	            //for each file under orders folder
	            for (File file : orderFiles) {
	                setCurrentOrdersFile(".\\orders\\" + file.getName(), Action.DISPLAY);

	                //write each order to EXPORT_FILE
	                ordersList.forEach(order -> 
	                {
	                    String orderAsText = marshallOrder(order) + ", " + getDateAsString(file);
	                    out.println(orderAsText);
	                    out.flush();
	                });
	            }
	        } 
	        catch (IOException e) 
	        {
	            throw new FlooringMasteryPersistenceException("Error. Could not write data to file.");
	        }
	    }

	    private String marshallOrder(Order order) 
	    {
	        String orderAsText = order.getOrderNumber() + DELIMITER;
	        orderAsText += order.getCustomerName() + DELIMITER;
	        orderAsText += order.getState() + DELIMITER;
	        orderAsText += order.getTaxRate() + DELIMITER;
	        orderAsText += order.getProductType() + DELIMITER;
	        orderAsText += order.getArea() + DELIMITER;
	        orderAsText += order.getCostPerSquareFoot() + DELIMITER;
	        orderAsText += order.getLaborCostPerSquareFoot() + DELIMITER;
	        orderAsText += order.getMaterialCost() + DELIMITER;
	        orderAsText += order.getLaborCost() + DELIMITER;
	        orderAsText += order.getTax() + DELIMITER;
	        orderAsText += order.getTotal();
	        return orderAsText;
	    }

	    private Order unmarshallOrder(String orderAsText) 
	    {
	        String[] orderTokens = orderAsText.split(DELIMITER);
	        Order currentOrder = new Order();
	        currentOrder.setOrderNumber(Integer.parseInt(orderTokens[0]));
	        currentOrder.setCustomerName(orderTokens[1]);
	        currentOrder.setState(orderTokens[2]);
	        currentOrder.setTaxRate(new BigDecimal(orderTokens[3]));
	        currentOrder.setProductType(orderTokens[4]);
	        currentOrder.setArea(new BigDecimal(orderTokens[5]));
	        currentOrder.setCostPerSquareFoot(new BigDecimal(orderTokens[6]));
	        currentOrder.setLaborCostPerSquareFoot(new BigDecimal(orderTokens[7]));
	        currentOrder.setMaterialCost(new BigDecimal(orderTokens[8]));
	        currentOrder.setLaborCost(new BigDecimal(orderTokens[9]));
	        currentOrder.setTax(new BigDecimal(orderTokens[10]));
	        currentOrder.setTotal(new BigDecimal(orderTokens[11]));
	        return currentOrder;
	    }

	    private State unmarshallState(String stateAsText) 
	    {
	        String[] stateTokens = stateAsText.split(DELIMITER);
	        State currentState = new State();
	        currentState.setStateAbbreviation(stateTokens[0]);
	        currentState.setStateName(stateTokens[1]);
	        currentState.setTaxRate(new BigDecimal(stateTokens[2]));
	        return currentState;
	    }

	    private Product unmarshallProduct(String productAsText)
	    {
	        String[] productToken = productAsText.split(DELIMITER);
	        Product product = new Product();
	        product.setProductType(productToken[0]);
	        product.setCostPerSquareFoot(new BigDecimal(productToken[1]));
	        product.setLaborCostPerSquareFoot(new BigDecimal(productToken[2]));
	        return product;
	    }
	}