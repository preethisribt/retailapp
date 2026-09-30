package com.preethisri.retailapp.Mapper;

import com.preethisri.retailapp.DTO.Request.CartItem.CartItemDTORequest;
import com.preethisri.retailapp.DTO.Response.CartItem.CartItemDTOResponse;
import com.preethisri.retailapp.Entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    @Mapping(source = "id",target = "cartItemId")
    @Mapping(source = "cart.id",target = "cartId")
    @Mapping(source = "product.id",target = "productId")
    @Mapping(source = "product.productName",target = "productName")
    CartItemDTOResponse toDTO(CartItem cartItem);

    CartItem toEntity(CartItemDTORequest cartItemDTORequest);
}
