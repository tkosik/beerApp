package com.example.BeerApp.configuration;

import com.github.javafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FakerConfig {

    @Bean
    public Faker faker() {
        System.out.println("Creating fake data...");
        return new Faker();
    }
}
