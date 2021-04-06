package com.jesua.registration.controller;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ImportAutoConfiguration({
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        TransactionAutoConfiguration.class,
        JacksonAutoConfiguration.class,
        HttpMessageConvertersAutoConfiguration.class
})

@TestConfiguration
@ComponentScan(basePackages = "com.jesua.registration")
@EntityScan(basePackages = "com.jesua.registration.entity")
@EnableJpaRepositories(basePackages = "com.jesua.registration.repository")
public class IntegrationTestConfig {
}
