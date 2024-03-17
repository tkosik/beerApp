package com.example.BeerApp.repositories;

import com.example.BeerApp.entities.Beer;
import com.example.BeerApp.model.BeerStyle;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class BeerRepositoryTest {

    @Autowired
    BeerRepository beerRepository;

    @Test
    void testSaveBeerWithLongName() {

        assertThrows(ConstraintViolationException.class, () -> {
            Beer savedBeer = beerRepository.save(Beer.builder()
                    .beerName("12345678901234567890123456789012345678901234567890123456789012345678901234567890")
                    .price(new BigDecimal("2.00"))
                    .beerStyle(BeerStyle.GOSE)
                    .upc("TEST")
                    .build());

            beerRepository.flush();

            assertThat(savedBeer).isNotNull();
            assertThat(savedBeer.getId()).isNotNull();
        });
    }

    @Test
    void testSaveBeer() {
        Beer savedBeer = beerRepository.save(Beer.builder()
                .beerName("My beer")
                .price(new BigDecimal("2.00"))
                .beerStyle(BeerStyle.GOSE)
                .upc("TEST")
                .build());

        beerRepository.flush();

        assertThat(savedBeer).isNotNull();
        assertThat(savedBeer.getId()).isNotNull();
    }
}