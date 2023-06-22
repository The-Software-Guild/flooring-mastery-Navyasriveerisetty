package com.flooringmastery;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.flooringmastery.controller.FlooringMasteryController;

public class FlooringMasteryApplication 
{

    public static void main(String[] args) throws InterruptedException 
    {

        AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext();
        appContext.scan("com.flooringmastery");
        appContext.refresh();

        FlooringMasteryController controller = appContext.getBean("flooringMasteryController", FlooringMasteryController.class);
        controller.run();
    }
}