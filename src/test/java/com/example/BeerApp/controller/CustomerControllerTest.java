package com.example.BeerApp.controller;

import com.example.BeerApp.controllers.CustomerController;
import com.example.BeerApp.exceptions.NotFoundException;
import com.example.BeerApp.model.CustomerDTO;
import com.example.BeerApp.services.CustomerService;
import com.example.BeerApp.services.CustomerServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    private final Faker faker = new Faker();

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CustomerService customerService;
    CustomerServiceImpl customerServiceImpl;

    @BeforeEach
    void setUp() {
        customerServiceImpl = new CustomerServiceImpl(faker);
    }

    @Test
    void getCustomerByIdNotFound() throws Exception {
        given(customerService.getCustomerById(any(UUID.class)))
                .willThrow(NotFoundException.class);

        mockMvc.perform(get("/api/v1/customer/", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }


    @Test
    @SneakyThrows
    void deleteCustomerByIdTest() {
        CustomerDTO testCustomer = customerServiceImpl.listAllCustomers().get(0);

        mockMvc
                .perform(delete("/api/v1/customer/" + testCustomer.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        ArgumentCaptor<UUID> argumentCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(customerService).deleteCustomerById(argumentCaptor.capture());

        assertThat(testCustomer.getId()).isEqualTo(argumentCaptor.getValue());
    }

    @Test
    @SneakyThrows
    public void updateCustomer() {
        CustomerDTO testCustomer = customerServiceImpl.listAllCustomers().get(0);

        given(customerService.updateCustomerById(eq(testCustomer.getId()), any(CustomerDTO.class)))
                .willReturn(Optional.ofNullable(customerServiceImpl.listAllCustomers().get(1)));

        mockMvc
                .perform(put("/api/v1/customer/" + testCustomer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCustomer))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(customerService).updateCustomerById(eq(testCustomer.getId()), any(CustomerDTO.class));
    }

    @Test
    @SneakyThrows
    public void createCustomerTest() {
        CustomerDTO testCustomer = customerServiceImpl.listAllCustomers().get(0);

        given(customerService.saveCustomer(any(CustomerDTO.class)))
                .willReturn(customerServiceImpl.listAllCustomers().get(1));

        mockMvc
                .perform(post("/api/v1/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCustomer))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    @SneakyThrows
    public void getCustomerByIdTest() {
        CustomerDTO testCustomer = customerServiceImpl.listAllCustomers().get(0);
        given(customerService.getCustomerById(testCustomer.getId()))
                .willReturn(customerServiceImpl.getCustomerById(testCustomer.getId()));

        mockMvc
                .perform(get("/api/v1/customer/" + testCustomer.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName", is(testCustomer.getCustomerName())))
                .andExpect(jsonPath("$.customerLastName", is(testCustomer.getCustomerLastName())))
                .andExpect(jsonPath("$.version", is(testCustomer.getVersion())));
    }

    @Test
    @SneakyThrows
    public void getCustomerListTest() {
        given(customerService.listAllCustomers())
                .willReturn(customerServiceImpl.listAllCustomers());

        mockMvc
                .perform(get("/api/v1/customer").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(3)));
    }
}
