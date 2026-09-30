package com.preethisri.retailapp.Controller;

import com.preethisri.retailapp.DTO.Request.CartItem.CartItemDTOPatchRequest;
import com.preethisri.retailapp.DTO.Request.CartItem.CartItemDTORequest;
import com.preethisri.retailapp.DTO.Response.CartItem.CartItemDTOResponse;
import com.preethisri.retailapp.Service.CartItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/carts")
@Tag(
        name = "Cart Item",
        description = "APIs for managing products in the cart item"
)
public class CartItemController {
    private final CartItemService cartItemService;

    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartItemDTOResponse> addCartItem(@PathVariable @Positive Long cartId, @Valid @RequestBody CartItemDTORequest cartItemRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemService.addProductToCart(cartId, cartItemRequest));
    }

    @GetMapping("/{cartId}/items/{cartItemId}")
    public ResponseEntity<CartItemDTOResponse> getCartItem(@PathVariable @Positive Long cartId, @PathVariable @Positive Long cartItemId) {
        return ResponseEntity.ok(cartItemService.getCartItemFromCart(cartId, cartItemId));
    }

    @PatchMapping("/{cartId}/items/{cartItemId}")
    public ResponseEntity<CartItemDTOResponse> updateCartItem(@PathVariable @Positive Long cartId, @PathVariable @Positive Long cartItemId, @Valid @RequestBody CartItemDTOPatchRequest cartItemRequest)
    {
        return ResponseEntity.ok(cartItemService.updateCartItem(cartId, cartItemId,cartItemRequest));
    }

    @DeleteMapping("/{cartId}/items/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable @Positive Long cartId, @PathVariable @Positive Long cartItemId)
    {
        cartItemService.deleteCartItem(cartId,cartItemId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
