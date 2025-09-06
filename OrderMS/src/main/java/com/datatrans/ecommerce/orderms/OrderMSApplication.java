package com.datatrans.ecommerce.orderms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
	    "com.datatrans.ecommerce.orderms", 
	    "com.operationHelper"             
	})
public class OrderMSApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderMSApplication.class, args);
    }
}
