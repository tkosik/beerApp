package com.example.BeerApp.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class BeerDTO {

    private UUID id;
    private Integer version;
    @NotBlank(message = "Name is required")
    @NotNull(message = "Name is required")
    private String beerName;
    @NotNull(message = "Beer Style is required")
    private BeerStyle beerStyle;
    @NotBlank(message = "UPC is required")
    @NotNull(message = "UPC is required")
    private String upc;
    private Integer quantityOnHand;
    @NotNull(message = "Price is required")
    @Min(value = 1, message = "Price should not be less than 1")
    private BigDecimal price;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;
}
