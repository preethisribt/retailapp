package com.preethisri.retailapp.DTO.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTOPatchRequest {
    @Schema(
            description = "Updated product name",
            example = "iPhone 16 Pro"
    )
    private String productName;

    @Schema(
            description = "Updated product price",
            example = "1299.99"
    )
    @Positive(message = "price has to be greater than 0")
    private BigDecimal price;

    @Schema(
            description = "Updated product description",
            example = "Latest Apple smartphone with advanced camera"
    )
    private String description;

    @Schema(
            description = "Updated available stock quantity",
            example = "50"
    )
    @PositiveOrZero(message = "stock has to be 0 or greater")
    private Integer stock;

    @Schema(
            description = "Updated product category",
            example = "Electronics"
    )
    @Size(max = 20)
    private String category;

    @Schema(
            description = "Updated storage capacity",
            example = "256GB"
    )
    @Size(max = 10)
    private String storage;

    @Schema(
            description = "Updated product colour",
            example = "Black"
    )
    @Size(max = 10)
    private String colour;
}
