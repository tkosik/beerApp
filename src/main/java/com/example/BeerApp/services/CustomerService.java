package com.example.BeerApp.services;

import com.example.BeerApp.model.CustomerDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {

    CustomerDTO getCustomerById(UUID id);

    List<CustomerDTO> listAllCustomers();

    CustomerDTO saveCustomer(CustomerDTO customer);

    Optional<CustomerDTO> updateCustomerById(UUID id, CustomerDTO customer);

    CustomerDTO patchCustomer(UUID id, CustomerDTO customer);

    void deleteCustomerById(UUID id);
}
