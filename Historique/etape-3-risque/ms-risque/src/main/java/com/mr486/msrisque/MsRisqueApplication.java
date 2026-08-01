package com.mr486.msrisque;

import com.mr486.commun.configuration.DocumentationApiConfiguration;
import com.mr486.commun.configuration.SecuriteApiConfiguration;
import com.mr486.commun.exception.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableFeignClients
@Import({
        SecuriteApiConfiguration.class,
        DocumentationApiConfiguration.class,
        GlobalExceptionHandler.class})
public class MsRisqueApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsRisqueApplication.class, args);
    }
}
