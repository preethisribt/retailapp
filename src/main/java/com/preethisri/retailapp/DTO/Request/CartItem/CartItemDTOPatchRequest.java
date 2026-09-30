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
public class CartItemDTOPatchRequest {
    @Schema(
            description = "Quantity of the product that needs to be updated",
            example = "5"
    )
    @NotNull(message = "Quantity can't be empty")
    @Positive(message = "Quantity must be at least 1")
    private Integer quantity;
}
