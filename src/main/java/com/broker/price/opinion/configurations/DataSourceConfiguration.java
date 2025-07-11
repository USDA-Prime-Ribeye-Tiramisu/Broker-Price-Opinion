package com.broker.price.opinion.configurations;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class DataSourceConfiguration {

    @Primary
    @Bean(name = "prodDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource prodDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "prodBackupDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.backup")
    public DataSource prodBackupDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "trinoDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.trino")
    public DataSource trinoDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "prodJdbcTemplate")
    public JdbcTemplate prodJdbcTemplate(@Qualifier("prodDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "prodBackupJdbcTemplate")
    public JdbcTemplate prodBackupJdbcTemplate(@Qualifier("prodBackupDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "trinoJdbcTemplate")
    public JdbcTemplate trinoJdbcTemplate(@Qualifier("trinoDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    private Properties additionalJpaProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        return properties;
    }
}