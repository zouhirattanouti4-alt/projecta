package com.zouhir.neobank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class NeoBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(NeoBankApplication.class, args);
    }

}
