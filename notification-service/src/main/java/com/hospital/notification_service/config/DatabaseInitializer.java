package com.hospital.notification_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@Slf4j
@Component
public class DatabaseInitializer implements BeanFactoryPostProcessor, EnvironmentAware {

    private static final String DB_NAME = "hms_notification";
    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String datasourceUrl = environment.getProperty("spring.datasource.url", "");
        String username = environment.getProperty("spring.datasource.username", "postgres");
        String password = environment.getProperty("spring.datasource.password", "");

        // Derive the admin URL by replacing the target DB name with the default 'postgres' DB
        String adminUrl = datasourceUrl.replaceAll("/" + DB_NAME + "(\\?.*)?$", "/postgres");

        try (Connection conn = DriverManager.getConnection(adminUrl, username, password);
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(
                    "SELECT 1 FROM pg_database WHERE datname = '" + DB_NAME + "'");

            if (!rs.next()) {
                stmt.execute("CREATE DATABASE " + DB_NAME);
                log.info("PostgreSQL database '{}' created successfully", DB_NAME);
            } else {
                log.debug("PostgreSQL database '{}' already exists", DB_NAME);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database '" + DB_NAME + "': " + e.getMessage(), e);
        }
    }
}
