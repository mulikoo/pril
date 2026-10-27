package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserApplication {
    public static void main(String[] args) {
        System.setProperty("server.port", "8081");
        SpringApplication.run(UserApplication.class, args);
    }
}