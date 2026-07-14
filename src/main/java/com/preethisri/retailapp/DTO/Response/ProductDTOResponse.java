package com.preethisri.retailapp.DTO.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTOResponse {

    @Schema(
            description = "Unique identifier of the product",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Name of the product",
            example = "iPhone 16 Pro"
    )
    private String productName;

    @Schema(
            description = "Product price",
            example = "1299.99"
    )
    private BigDecimal price;

    @Schema(
            description = "Product description",
            example = "Latest Apple smartphone with advanced camera features"
    )
    private String description;

    @Schema(
            description = "Available quantity in inventory",
            example = "50"
    )
    private Integer stock;

    @Schema(
            description = "Product category",
            example = "Electronics"
    )
    private String category;

    @Schema(
            description = "Product storage capacity",
            example = "256GB"
    )
    private String storage;

    @Schema(
            description = "Product colour",
            example = "Black"
    )
    private String colour;

    @Schema(
            description = "Unique stock keeping unit for product identification",
            example = "IPH-ELE-001"
    )
    private String sku;
}
