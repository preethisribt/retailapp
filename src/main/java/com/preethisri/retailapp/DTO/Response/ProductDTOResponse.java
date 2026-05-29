package com.preethisri.retailapp.DTO.Response;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTOResponse {
    private long id;
    private String productName;
    private BigDecimal price;
    private String description;
    private Integer stock;
    private String category;
    private String storage;
    private String colour;
    private String sku;
}
