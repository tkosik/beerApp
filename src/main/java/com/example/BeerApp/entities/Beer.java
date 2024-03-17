package com.example.BeerApp.entities;

import com.example.BeerApp.model.BeerStyle;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Beer {

    @Id
    @GeneratedValue(generator = "UUID", strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, columnDefinition = "varchar(36)", nullable = false, updatable = false)
    private UUID id;

    @Version
    private Integer version;

    @NotBlank(message = "Name is required")
    @NotNull(message = "Name is required")
    @Size(min = 3, max = 30, message = "Name should be between 3 and 30 characters")
    @Column(length = 30)
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
