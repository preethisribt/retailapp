package com.preethisri.retailapp.DTO.Request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTOPatchRequest {
    private String productName;

    @Positive(message = "price has to be greater than 0")
    private BigDecimal price;

    private String description;

    @PositiveOrZero(message = "stock has to be 0 or greater")
    private Integer stock;

    @Size(max = 20)
    private String category;

    @Size(max = 10)
    private String storage;

    @Size(max = 10)
    private String colour;
}
