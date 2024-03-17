package com.example.BeerApp.services;

import com.example.BeerApp.entities.Customer;
import com.example.BeerApp.exceptions.InvalidDataException;
import com.example.BeerApp.exceptions.NotFoundException;
import com.example.BeerApp.mappers.CustomerMapper;
import com.example.BeerApp.model.CustomerDTO;
import com.example.BeerApp.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Primary
@Slf4j
public class CustomerServiceJPA implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;


    @Override
    public CustomerDTO getCustomerById(UUID id) {
        return customerRepository.findById(id)
                .map(customerMapper::customerToCustomerDTO)
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + id));
    }

    @Override
    public List<CustomerDTO> listAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::customerToCustomerDTO)
                .toList();
    }

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customer) {
        return customerMapper.customerToCustomerDTO(customerRepository.save((customerMapper.customerDTOToCustomer(customer))));
    }

    @Override
    public Optional<CustomerDTO> updateCustomerById(UUID id, CustomerDTO customerDTO) {
        Customer existingCustomer = customerRepository.findById(id).orElse(null);

        List<String> missingFields = new ArrayList<>();

        if (customerDTO.getCustomerName() == null) {
            missingFields.add("customerName");
        }
        if (customerDTO.getCustomerLastName() == null) {
            missingFields.add("customerLastName");
        }
        if (customerDTO.getVersion() == null) {
            missingFields.add("version");
        }

        if (!missingFields.isEmpty()) {
            throw new InvalidDataException("Missing required fields: " + String.join(", ", missingFields));
        }

        if (existingCustomer != null) {
            log.info("Updating customer with id: " + id);
            existingCustomer.setCustomerName(customerDTO.getCustomerName());
            existingCustomer.setCustomerLastName(customerDTO.getCustomerLastName());
            existingCustomer.setVersion(customerDTO.getVersion());
            existingCustomer.setLastModifiedDate(LocalDateTime.now());
            return Optional.ofNullable(customerMapper.customerToCustomerDTO(customerRepository.save(existingCustomer)));
        } else {
            log.info("Creating customer with id: " + id);
            return Optional.ofNullable(customerMapper.customerToCustomerDTO(customerRepository.save(
                    (customerMapper.customerDTOToCustomer(customerDTO)))));
        }
    }


    @Override
    public CustomerDTO patchCustomer(UUID id, CustomerDTO customerDTO) {
        return customerRepository.findById(id)
                .map(existingCustomer -> {
                    if (customerDTO.getCustomerName() != null) {
                        existingCustomer.setCustomerName(customerDTO.getCustomerName());
                    }
                    if (customerDTO.getCustomerLastName() != null) {
                        existingCustomer.setCustomerLastName(customerDTO.getCustomerLastName());
                    }
                    if (customerDTO.getVersion() != null) {
                        existingCustomer.setVersion(customerDTO.getVersion());
                    }
                    existingCustomer.setLastModifiedDate(LocalDateTime.now());
                    Customer savedCustomer = customerRepository.save(existingCustomer);
                    return customerMapper.customerToCustomerDTO(savedCustomer);
                })
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + id));
    }

    @Override
    public void deleteCustomerById(UUID id) {
        customerRepository.findById(id)
                .ifPresentOrElse(customerRepository::delete,
                        () -> {
                            throw new NotFoundException("Customer not found with id: " + id);
                        });
    }
}
