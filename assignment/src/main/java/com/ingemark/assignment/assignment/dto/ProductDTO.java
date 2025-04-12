package com.ingemark.assignment.assignment.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductDTO {

    @NotBlank(message = "Name cannot be blank.")
    @NotNull(message = "Name cannot be null.")
    @Size(min = 3, max = 30)
    private String name;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price value must be more then 0!")
    @Digits(integer=7, fraction=2, message = "Price must be only 2 decimal values e.g. - .99")
    private BigDecimal priceEur;

    @NotNull(message = "Item status cannot be null. Can be only true or false!")
    private Boolean isAvailable;

}
