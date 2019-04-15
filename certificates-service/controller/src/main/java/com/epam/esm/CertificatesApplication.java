package com.epam.esm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class CertificatesApplication {
    public static void main(String[] args) {
        SpringApplication.run(CertificatesApplication.class, args);
    }
}
