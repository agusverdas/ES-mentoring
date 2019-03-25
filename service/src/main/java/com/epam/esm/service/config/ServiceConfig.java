package com.epam.esm.service.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan(value = "com.epam.esm.service")
public class ServiceConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
