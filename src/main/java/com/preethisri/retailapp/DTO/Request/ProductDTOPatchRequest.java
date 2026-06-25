package com.preethisri.retailapp.DTO.Request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTORequest {

    @NotBlank(message = "Product name is required")
    private String productName;

    @NotNull(message = "price is required")
    @Positive(message = "price has to be greater than 0")
    private BigDecimal price;

    private String description;

    @PositiveOrZero(message = "stock has to be 0 or greater")
    @NotNull(message = "stock is required")
    private Integer stock;

    @NotNull(message = "category is required")
    @Size(max = 20)
    private String category;

    @NotNull(message = "storage is required")
    @Size(max = 10)
    private String storage;

    @NotNull(message = "colour is required")
    @Size(max = 10)
    private String colour;
    private String sku;
}
