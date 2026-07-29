package com.preethisri.retailapp.Mapper;

import com.preethisri.retailapp.DTO.Request.Product.ProductDTOPatchRequest;
import com.preethisri.retailapp.DTO.Request.Product.ProductDTORequest;
import com.preethisri.retailapp.DTO.Response.Product.ProductDTOResponse;
import com.preethisri.retailapp.Entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTOResponse toDTO(Product product);
    Product toEntity(ProductDTORequest productDTORequest);
    Product toPatchEntity(ProductDTOPatchRequest request);
}
