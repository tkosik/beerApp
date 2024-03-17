package com.example.BeerApp.controller;

import com.example.BeerApp.controllers.BeerController;
import com.example.BeerApp.entities.Beer;
import com.example.BeerApp.exceptions.NotFoundException;
import com.example.BeerApp.model.BeerDTO;
import com.example.BeerApp.model.BeerStyle;
import com.example.BeerApp.services.BeerService;
import com.example.BeerApp.services.BeerServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.example.BeerApp.controllers.BeerController.BEER_PATH;
import static com.example.BeerApp.controllers.BeerController.BEER_PATH_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerController.class)
class BeerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Captor
    ArgumentCaptor<BeerDTO> beerArgumentCaptor;

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;

    @MockBean
    BeerService beerService;

    BeerServiceImpl beerServiceImpl;


    @BeforeEach
    void setUp() {
        beerServiceImpl = new BeerServiceImpl();
    }

    @Test
    void getBeerByIdNotFound() throws Exception {

        given(beerService.getBeerById(any(UUID.class)))
                .willThrow(NotFoundException.class);

        mockMvc.perform(get(BEER_PATH_ID, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void patchBeersTest() {
        BeerDTO beer = beerServiceImpl.listBeers().getBeers().get(0);

        given(beerService.patchBeerById(any(UUID.class), any(BeerDTO.class)))
                .willReturn(Optional.ofNullable(beerServiceImpl.listBeers().getBeers().get(1)));

        mockMvc
                .perform(patch(BEER_PATH_ID, beer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(beerService).patchBeerById(uuidArgumentCaptor.capture(), beerArgumentCaptor.capture());

        assertThat(beer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
        assertThat(beer.getBeerName()).isEqualTo(beerArgumentCaptor.getValue().getBeerName());
    }


    @Test
    @SneakyThrows
    void deleteBeerByIdTest() {
        BeerDTO beer = beerServiceImpl.listBeers().getBeers().get(0);

        given(beerService.deleteBeerById(any(UUID.class)))
                .willReturn(true);

        mockMvc
                .perform(delete(BEER_PATH_ID, beer.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(beerService).deleteBeerById(uuidArgumentCaptor.capture());

        assertThat(beer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
    }

    @Test
    @SneakyThrows
    void updateBeerTest() {
        BeerDTO beer = beerServiceImpl.listBeers().getBeers().get(0);

        given(beerService.updateBeerById(any(UUID.class), any(BeerDTO.class)))
                .willReturn(Optional.ofNullable(beerServiceImpl.listBeers().getBeers().get(1)));

        mockMvc
                .perform(put(BEER_PATH_ID, beer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(beerService).updateBeerById(any(UUID.class), any(BeerDTO.class));
    }

    @Test
    @SneakyThrows
    void createBeerNullNameTest() {
        BeerDTO beer = BeerDTO.builder()
                .beerName(null)
                .beerStyle(null)
                .upc(null)
                .price(null)
                .quantityOnHand(122)
                .build();

        MvcResult mvcResult = mockMvc
                .perform(post(BEER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(4)))
                .andExpect(jsonPath("$.beerName", is("Name is required")))
                .andExpect(jsonPath("$.beerStyle", is("Beer Style is required")))
                .andExpect(jsonPath("$.upc", is("UPC is required")))
                .andExpect(jsonPath("$.price", is("Price is required")))
                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    @SneakyThrows
    void createNewBeerTest() {
        Beer beer = Beer.builder()
                .beerName("JOHN")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("12356")
                .price(new BigDecimal("12.99"))
                .quantityOnHand(122)
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        given(beerService.saveBeer(any(BeerDTO.class)))
                .willReturn(beerServiceImpl.listBeers().getBeers().get(1));

        mockMvc
                .perform(post(BEER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

    }

    @Test
    @SneakyThrows
    void listBeersTest() {
        given(beerService.listBeers())
                .willReturn(beerServiceImpl.listBeers());

        mockMvc
                .perform(get(BEER_PATH).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.beers[0].id", is(beerServiceImpl.listBeers().getBeers().get(0).getId().toString())))
                .andExpect(jsonPath("$.size", is(2)))
                .andExpect(jsonPath("$.beers.length()", is(2)));
    }

    @Test
    @SneakyThrows
    void getBeerByIdTest() {
        BeerDTO testBeer = beerServiceImpl.listBeers().getBeers().get(0);

        given(beerService.getBeerById(testBeer.getId()))
                .willReturn(Optional.of(testBeer));

        mockMvc.perform(get(BEER_PATH_ID, testBeer.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().stringValues("Location", BEER_PATH + testBeer.getId()))
                .andExpect(jsonPath("$.id", is(testBeer.getId().toString())))
                .andExpect(jsonPath("$.price", equalTo(testBeer.getPrice().doubleValue())))
                .andExpect(jsonPath("$.beerName", is(testBeer.getBeerName())));
    }
}
