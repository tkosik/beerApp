package com.example.BeerApp.controller;

import com.example.BeerApp.controllers.BeerController;
import com.example.BeerApp.entities.Beer;
import com.example.BeerApp.exceptions.NotFoundException;
import com.example.BeerApp.mappers.BeerMapper;
import com.example.BeerApp.model.BeerDTO;
import com.example.BeerApp.model.BeerStyle;
import com.example.BeerApp.repositories.BeerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.example.BeerApp.controllers.BeerController.BEER_PATH_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BeerControllerIT {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;
    @Autowired
    private BeerController beerController;
    @Autowired
    private BeerRepository beerRepository;
    @Autowired
    private BeerMapper beerMapper;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    @SneakyThrows
    void patchBeersTest() {
        Beer beer = beerRepository.findAll().get(0);

        Map<String, String> beerMap = new HashMap<>();
        beerMap.put("beerName", "Updated Beer NameUpdated Beer NameUpdated Beer NameUpdated Beer NameUpdated Beer NameUpdated Beer NameUpdated Beer NameUpdated Beer NameUpdated Beer Name");

        mockMvc
                .perform(patch(BEER_PATH_ID, beer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerMap))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    @Transactional
    @Rollback
    void deleteBeerByIdFound() {
        Beer beer = beerRepository.findAll().get(0);
        ResponseEntity<?> response = beerController.deleteBeerById(beer.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(beerRepository.findById(beer.getId())).isEmpty();
    }

    @Test
    void deleteBeerByIdNotFound() {
        assertThrows(NotFoundException.class, () -> beerController.deleteBeerById(UUID.randomUUID()));
    }

    @Test
    @Transactional
    @Rollback
    void updateExistingBeer() {
        Beer beer = beerRepository.findAll().get(0);
        System.out.println(beer.getBeerName());
        BeerDTO beerDTO = beerMapper.beerToBeerDto(beer);
        final String newBeerName = "Updated";
        beerDTO.setBeerName(newBeerName);

        ResponseEntity<?> response = beerController.updateBeerById(beer.getId(), beerDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Beer updatedBeer = beerRepository.findById(beer.getId()).get();
        assertThat(updatedBeer.getBeerName()).isEqualTo(newBeerName);
    }

    @Test
    @Transactional
    @Rollback
    void updateBeerNotFound() {
        BeerDTO beerDTO = BeerDTO.builder()
                .beerName("Galaxy Cat")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("12356")
                .price(new BigDecimal("12.99"))
                .version(1)
                .quantityOnHand(122)
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        assertThrows(NotFoundException.class, () -> beerController.updateBeerById(UUID.randomUUID(), beerDTO));
    }

    @Test
    @Rollback
    @Transactional
    void testSaveBeer() {
        BeerDTO beerDTO = BeerDTO.builder()
                .beerName("Galaxy Cat")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("12356")
                .price(new BigDecimal("12.99"))
                .version(1)
                .quantityOnHand(122)
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        ResponseEntity<BeerDTO> response = beerController.saveBeer(beerDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        String[] locationUUID = response.getHeaders().getLocation().getPath().split("/");
        UUID beerId = UUID.fromString(locationUUID[4]);
        System.out.println(beerId);
        Beer beer = beerRepository.findById(beerId).get();

        System.out.println(beer);
        assertThat(beer.getBeerName()).isEqualTo(beerDTO.getBeerName());
    }

    @Test
    void testListBeers() {
        List<BeerDTO> dtos = beerController.listBeers().getBeers();

        assertThat(dtos.size()).isEqualTo(3);
    }

    @Rollback
    @Transactional
    @Test
    void testEmptyList() {
        beerRepository.deleteAll();
        List<BeerDTO> dtos = beerController.listBeers().getBeers();

        assertThat(dtos.size()).isEqualTo(0);
    }

    @Test
    @Transactional
    void testGetBeerById() {
        Beer beer = beerRepository.findAll().get(0);
        ResponseEntity<BeerDTO> dto = beerController.getBeerById(beer.getId());

        assertThat(dto).isNotNull();
    }

    @Test
    @Transactional
    void testGetBeerByIdThrowsException() {

        UUID id = UUID.randomUUID();
        assertThatThrownBy(() -> beerController.getBeerById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Beer not found with id: " + id);

        assertThrows(NotFoundException.class, () -> beerController.getBeerById(id));
    }
}