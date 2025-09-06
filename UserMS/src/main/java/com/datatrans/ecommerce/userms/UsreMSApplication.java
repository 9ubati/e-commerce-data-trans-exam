package com.datatrans.ecommerce.userms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
	    "com.datatrans.ecommerce.userms", // your app
	    "com.operationHelper"             // <- helper lib (note the exact case)
	})
public class UsreMSApplication {
    public static void main(String[] args) {
        SpringApplication.run(UsreMSApplication.class, args);
    }
}
