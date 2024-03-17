package com.example.BeerApp.controllers;

import com.example.BeerApp.model.CustomerDTO;
import com.example.BeerApp.services.CustomerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer")
@AllArgsConstructor
public class CustomerController {

    private CustomerService customerService;

    @RequestMapping("")
    public List<CustomerDTO> listAllCustomers() {
        return customerService.listAllCustomers();
    }

    @RequestMapping("/{customerId}")
    public CustomerDTO getCustomerById(@PathVariable("customerId") UUID customerId) {
        return customerService.getCustomerById(customerId);
    }

    @PostMapping
    public ResponseEntity saveCustomer(@Valid @RequestBody CustomerDTO customer) {
        customer.setId(UUID.randomUUID());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, "/api/v1/customer/" + customer.getId());
        return new ResponseEntity(customerService.saveCustomer(customer), headers, HttpStatus.CREATED);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<Map<String, Object>> updateCustomerById(@PathVariable("customerId") UUID customerId, @Valid @RequestBody CustomerDTO customer) {
        return customerService.updateCustomerById(customerId, customer)
                .map(updatedCustomer -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "success");
                    response.put("message", "Customer updated successfully");
                    response.put("customer", updatedCustomer);
                    return ResponseEntity.ok(response);
                }).orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "error");
                    response.put("message", "Customer not found");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    @PatchMapping("/{customerId}")
    public ResponseEntity<CustomerDTO> patchCustomerById(@PathVariable("customerId") UUID customerId, @RequestBody CustomerDTO customer) {
        return new ResponseEntity<>(customerService.patchCustomer(customerId, customer), HttpStatus.OK);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomerById(@PathVariable("customerId") UUID customerId) {
        customerService.deleteCustomerById(customerId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
