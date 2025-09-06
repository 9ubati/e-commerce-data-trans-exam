package com.datatrans.ecommerce.productms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
	    "com.datatrans.ecommerce.productms", 
	    "com.operationHelper"             
	})
public class ProductMSApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductMSApplication.class, args);
    }
}
