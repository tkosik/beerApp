package com.example.BeerApp.controller;

import com.example.BeerApp.controllers.CustomerController;
import com.example.BeerApp.entities.Customer;
import com.example.BeerApp.exceptions.NotFoundException;
import com.example.BeerApp.model.CustomerDTO;
import com.example.BeerApp.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CustomerControllerIT {

    @Autowired
    CustomerController customerController;

    @Autowired
    CustomerRepository customerRepository;

    @Test
    @Transactional
    @Rollback
    void createNewCustomerTest() {
        CustomerDTO customer = CustomerDTO.builder()
                .customerName("John Doe")
                .version(1).build();

        ResponseEntity<?> response = customerController.saveCustomer(customer);
        assertThat(response).isNotNull();

        Optional<Customer> savedCustomer = customerRepository.findById(customer.getId());

        savedCustomer.ifPresent(c -> {
            assertThat(c.getCustomerName()).isEqualTo("John Doe");
        });
    }

    @Test
    @Transactional
    @Rollback
    void updateCustomerByIdTest() {
        Customer customerOptional = customerRepository.findAll().get(0);
        LocalDateTime lastModified = customerOptional.getLastModifiedDate();
        Integer currentVersion = customerOptional.getVersion();

        final String customerNewName = "Jane Doe";
        final String lastName = "Jane Doe";

        CustomerDTO updateCustomerData = CustomerDTO.builder()
                .customerName(customerNewName)
                .customerLastName(lastName)
                .version(currentVersion + 1)
                .build();
        ResponseEntity<?> updateResponse = customerController.updateCustomerById(customerOptional.getId(), updateCustomerData);
        assertThat(updateResponse).isNotNull();

        Optional<Customer> updatedCustomer = customerRepository.findById(customerOptional.getId());
        updatedCustomer.ifPresent(c -> {
            assertThat(c.getCustomerName()).isEqualTo(customerNewName);
            assertThat(c.getLastModifiedDate()).isAfterOrEqualTo(lastModified);
            assertThat(c.getVersion()).isEqualTo(currentVersion + 1);
        });
    }

    @Test
    @Transactional
    @Rollback
    void createCustomerWithPutMethodTest() {
        final UUID customerId = UUID.randomUUID();
        final String exampeString = "Test";

        CustomerDTO updateCustomerData = CustomerDTO.builder()
                .customerName(exampeString)
                .customerLastName(exampeString)
                .version(1)
                .build();
        ResponseEntity<?> updateResponse = customerController.updateCustomerById(customerId, updateCustomerData);
        assertThat(updateResponse).isNotNull();

        Optional<Customer> updatedCustomer = customerRepository.findById(customerId);

        updatedCustomer.ifPresent(c -> {
            assertThat(c.getCustomerName()).isEqualTo(exampeString);
            assertThat(c.getCustomerLastName()).isEqualTo(exampeString);
            assertThat(c.getVersion()).isEqualTo(1);
        });
    }

    @Test
    @Transactional
    @Rollback
    void patchCustomerTest() {
        Customer customer = customerRepository.findAll().get(0);
        CustomerDTO customerDTO = CustomerDTO.builder()
                .customerName("Jane Doe")
                .build();
        ResponseEntity<?> response = customerController.patchCustomerById(customer.getId(), customerDTO);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        Optional<Customer> updatedCustomer = customerRepository.findById(customer.getId());
        updatedCustomer.ifPresent(c -> {
            assertThat(c.getCustomerName()).isEqualTo("Jane Doe");
        });
    }

    @Test
    @Transactional
    @Rollback
    void patchCustomerNotFoundTest() {

        final UUID uuid = UUID.randomUUID();

        assertThatThrownBy(() -> customerController.patchCustomerById(uuid, CustomerDTO.builder().build()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Customer not found with id: " + uuid);
    }

    @Test
    @Transactional
    @Rollback
    void deleteCustomerByIdTest() {
        UUID customerId = customerRepository.findAll().get(0).getId();
        customerController.deleteCustomerById(customerId);

        assertThatThrownBy(() -> customerController.getCustomerById(customerId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Customer not found with id: " + customerId);
    }

    @Test
    void listAllCustomersTest() {
        List<CustomerDTO> customers = customerController.listAllCustomers();
        assertThat(customers.size()).isEqualTo(10);
    }

    @Test
    void getCustomerByIdTest() {
        UUID customerId = customerRepository.findAll().get(0).getId();

        CustomerDTO customer = customerController.getCustomerById(customerId);
        assertThat(customer).isNotNull();
    }

    @Test
    void getCustomerByIdThrowsExceptionTest() {
        UUID customerId = UUID.randomUUID();
        assertThatThrownBy(() -> customerController.getCustomerById(customerId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Customer not found with id: " + customerId);

        assertThrows(NotFoundException.class, () -> customerController.getCustomerById(customerId));
    }

    @Test
    @Transactional
    @Rollback
    void emptyListTest() {
        customerRepository.deleteAll();
        List<CustomerDTO> customers = customerController.listAllCustomers();
        assertThat(customers.size()).isEqualTo(0);
    }
}