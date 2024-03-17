package com.example.BeerApp.services;

import com.example.BeerApp.model.CustomerDTO;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final HashMap<UUID, CustomerDTO> customers = new HashMap<>();

    public CustomerServiceImpl(Faker faker) {
        CustomerDTO customer;
        for (int i = 0; i < 3; i++) {
            customer = CustomerDTO.builder()
                    .id(UUID.randomUUID())
                    .customerName(faker.name().firstName())
                    .customerLastName(faker.name().lastName())
                    .version(1)
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .build();
            customers.put(customer.getId(), customer);
        }
    }

    @Override
    public CustomerDTO getCustomerById(UUID id) {
        return customers.get(id);
    }

    @Override
    public List<CustomerDTO> listAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customer) {
        customers.put(customer.getId(), customer);
        return customers.get(customer.getId());
    }

    @Override
    public Optional<CustomerDTO> updateCustomerById(UUID id, CustomerDTO customer) {
        AtomicReference<CustomerDTO> updatedCustomerRef = new AtomicReference<>();

        customers.computeIfPresent(id, (key, existingCustomer) -> {

                    existingCustomer.setCustomerName(customer.getCustomerName());
                    existingCustomer.setCustomerLastName(customer.getCustomerLastName());
                    existingCustomer.setVersion(customer.getVersion());
                    existingCustomer.setLastModifiedDate(LocalDateTime.now());
                    updatedCustomerRef.set(existingCustomer); // Store the updated customer

                    return existingCustomer;
                }
        );
        return Optional.ofNullable(updatedCustomerRef.get());
    }

    @Override
    public CustomerDTO patchCustomer(UUID id, CustomerDTO customer) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteCustomerById(UUID id) {
        customers.remove(id);
    }
}
