package com.preethisri.retailapp.DTO.Response.CartItem;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class CartItemDTOResponse {
  private Long cartId;
  private Long cartItemId;
  private Long productId;
  private String productName;
  private Integer quantity;
  private BigDecimal unitPrice;
  private BigDecimal amount;
}
