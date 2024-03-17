package com.example.BeerApp.bootstrap;

import com.example.BeerApp.entities.Beer;
import com.example.BeerApp.entities.Customer;
import com.example.BeerApp.model.BeerStyle;
import com.example.BeerApp.repositories.BeerRepository;
import com.example.BeerApp.repositories.CustomerRepository;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final BeerRepository beerRepository;
    private final CustomerRepository customerRepository;
    private final Faker faker;


    @Override
    public void run(String... args) {
        loadBeerData();
        loadCustomerData();

    }

    private void loadBeerData() {
        if (beerRepository.count() == 0) {
            Beer beer = Beer.builder()
                    .beerName("Galaxy Cat")
                    .beerStyle(BeerStyle.PALE_ALE)
                    .upc("12356")
                    .price(new BigDecimal("12.99"))
                    .quantityOnHand(122)
                    .createdDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .build();

            Beer beer2 = Beer.builder()
                    .beerName("Hohoho Cat")
                    .beerStyle(BeerStyle.LAGER)
                    .upc("12356")
                    .price(new BigDecimal("11.99"))
                    .quantityOnHand(155)
                    .createdDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .build();

            Beer beer3 = Beer.builder()
                    .beerName("Zxcvasd Zadas")
                    .beerStyle(BeerStyle.LAGER)
                    .upc("79976")
                    .price(new BigDecimal("81.99"))
                    .quantityOnHand(153)
                    .createdDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .build();

            beerRepository.save(beer);
            beerRepository.save(beer2);
            beerRepository.save(beer3);
        }
    }

    private void loadCustomerData() {
        if (customerRepository.count() == 0) {
            Customer customer;
            for (int i = 0; i < 10; i++) {
                customer = Customer.builder()
                        .id(UUID.randomUUID())
                        .customerName(faker.name().firstName())
                        .customerLastName(faker.name().lastName())
                        .version(1)
                        .createdDate(LocalDateTime.now())
                        .lastModifiedDate(LocalDateTime.now())
                        .build();
                customerRepository.save(customer);
            }
        }
    }
}
