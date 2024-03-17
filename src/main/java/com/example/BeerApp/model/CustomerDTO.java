package com.example.BeerApp.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CustomerDTO {

    private UUID id;
    @NotBlank(message = "Customer name cannot be blank")
    private String customerName;
    @NotBlank(message = "Customer last name cannot be blank")
    private String customerLastName;
    private Integer version;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

}
