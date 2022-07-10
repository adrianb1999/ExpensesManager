package com.adrian99.expensesManager;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ExpensesManagerApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(ExpensesManagerApplication.class, args);
	}

	@Bean
	public OpenAPI openAPIConfig(){
		return new OpenAPI().info(apiInfo());
	}

	public Info apiInfo(){
		return new Info().title("Expenses manager documentation")
				.version("0.0.1");
	}
}
