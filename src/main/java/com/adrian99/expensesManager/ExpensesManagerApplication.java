package com.adrian99.expensesManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
public class ExpensesManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpensesManagerApplication.class, args);
	}

}
