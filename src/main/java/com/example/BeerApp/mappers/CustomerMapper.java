package com.example.BeerApp.mappers;

import com.example.BeerApp.entities.Customer;
import com.example.BeerApp.model.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {

    CustomerDTO customerToCustomerDTO(Customer customer);

    Customer customerDTOToCustomer(CustomerDTO customerDTO);
}
