package com.preethisri.retailapp.DTO.Request.Product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTORequest {
    @Schema(
            description = "Name of the product",
            example = "Samsung Galaxy S25"
    )
    @NotBlank(message = "Product name is required")
    private String productName;

    @Schema(
            description = "Product price",
            example = "999.99"
    )
    @NotNull(message = "price is required")
    @Positive(message = "price has to be greater than 0")
    private BigDecimal price;

    private String description;


    @Schema(
            description = "Available quantity",
            example = "100"
    )
    @PositiveOrZero(message = "stock has to be 0 or greater")
    @NotNull(message = "stock is required")
    private Integer stock;


    @Schema(
            description = "Product category",
            example = "Phone"
    )
    @NotNull(message = "category is required")
    @Size(max = 20)
    private String category;


    @Schema(
            description = "Product storage",
            example = "1TB"
    )
    @NotNull(message = "storage is required")
    @Size(max = 10)
    private String storage;


    @Schema(
            description = "Product colour",
            example = "Black"
    )
    @NotNull(message = "colour is required")
    @Size(max = 10)
    private String colour;
}
