package com.adrian99.expensesManager;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class PostgresTestContainer {

    @Container
    public static PostgreSQLContainer container = new PostgreSQLContainer()
            .withUsername("test")
            .withPassword("test")
            .withDatabaseName("db_test");

    @DynamicPropertySource
    static void proprieties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.datasource.username", container::getUsername);
    }
}
