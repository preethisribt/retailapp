package com.preethisri.retailapp.DTO.Request.CartItem;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CartItemDTORequest {
    @Schema(
            description = "Id of the Product need to be purchased",
            example = "1"
    )
    @NotNull(message = "Product ID can't be empty")
    private Long productId;

    @Schema(
            description = "Quantity of the product needs to be purchased",
            example = "10"
    )
    @NotNull(message = "Quantity can't be empty")
    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;
}
