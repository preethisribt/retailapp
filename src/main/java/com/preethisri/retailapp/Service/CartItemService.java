package com.preethisri.retailapp.Service;

import com.preethisri.retailapp.DTO.Request.CartItem.CartItemDTOPatchRequest;
import com.preethisri.retailapp.DTO.Request.CartItem.CartItemDTORequest;
import com.preethisri.retailapp.DTO.Response.CartItem.CartItemDTOResponse;
import com.preethisri.retailapp.Entity.Cart;
import com.preethisri.retailapp.Entity.CartItem;
import com.preethisri.retailapp.Entity.Product;
import com.preethisri.retailapp.Exception.InsufficientStockException;
import com.preethisri.retailapp.Exception.ProductAlreadyInCartException;
import com.preethisri.retailapp.Exception.ResourceNotFoundException;
import com.preethisri.retailapp.Mapper.CartItemMapper;
import com.preethisri.retailapp.Repository.CartItemRepository;
import com.preethisri.retailapp.Repository.CartRepository;
import com.preethisri.retailapp.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartItemService {
    private final CartItemMapper cartItemMapper;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CartItemDTOResponse  addProductToCart(Long cartId, CartItemDTORequest cartItemRequest) {
        Long productId = cartItemRequest.getProductId();

        Cart cart = checkCartExists(cartId);
        Product product = checkProductIsValid(productId);
        checkProductNotAlreadyInCart(cartId, productId);
        checkProductStockIsSufficient(product, cartItemRequest.getQuantity());

        CartItem cartItemEntity = cartItemMapper.toEntity(cartItemRequest);
        cartItemEntity.setCart(cart);
        cartItemEntity.setProduct(product);
        CartItem cartItem = cartItemRepository.save(cartItemEntity);

        CartItemDTOResponse cartItemDTOResponse = cartItemMapper.toDTO(cartItem);
        setUnitPriceAndAmountForCartItem(product, cartItem, cartItemDTOResponse);
        return cartItemDTOResponse;
    }

    private void setUnitPriceAndAmountForCartItem(Product product, CartItem cartItem, CartItemDTOResponse cartItemDTOResponse) {
        BigDecimal unitPrice = product.getPrice();
        BigDecimal amount = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
        cartItemDTOResponse.setUnitPrice(unitPrice);
        cartItemDTOResponse.setAmount(amount);
    }

    private Cart checkCartExists(Long cartId) {
        return cartRepository.findById(cartId).orElseThrow(() ->
        {
            log.warn("Cart not found with id {}", cartId);
            return new ResourceNotFoundException("Cart not found: " + cartId);
        });
    }

    private Product checkProductIsValid(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> {
                    log.warn("Product not found with id {}", productId);
                    return new ResourceNotFoundException("Product not found: " + productId);
                }
        );
    }

    private void checkProductStockIsSufficient(Product product, Integer quantity) {
        if (product.getStock() < quantity)
            throw new InsufficientStockException("Insufficient stock for product: " +
                    product.getProductName() + " Available stock " + product.getStock()
                    + " Requested stock: " + quantity);
    }

    private void checkProductNotAlreadyInCart(Long cartId, Long productId) {
        if (cartItemRepository.existsByCartIdAndProductId(cartId, productId)) {
            log.warn("Product ID {} already exists in the cart {}", productId, cartId);
            throw new ProductAlreadyInCartException("Product ID already exists in the cart: " + productId);
        }
    }


    public CartItemDTOResponse getCartItemFromCart(Long cartId, Long cartItemId) {
        CartItem cartItem = checkCartItemExistsUnderCart(cartId, cartItemId);

        CartItemDTOResponse cartItemDTOResponse = cartItemMapper.toDTO(cartItem);
        setUnitPriceAndAmountForCartItem(cartItem.getProduct(), cartItem, cartItemDTOResponse);
        return cartItemDTOResponse;
    }

    @Transactional
    public CartItemDTOResponse updateCartItem(Long cartId, Long cartItemId, CartItemDTOPatchRequest request) {
        CartItem cartItem = checkCartItemExistsUnderCart(cartId, cartItemId);
        checkProductStockIsSufficient(cartItem.getProduct(), request.getQuantity());
        cartItem.setQuantity(request.getQuantity());

        CartItemDTOResponse responseDTO = cartItemMapper.toDTO(cartItem);
        setUnitPriceAndAmountForCartItem(cartItem.getProduct(), cartItem, responseDTO);

        return responseDTO;
    }


    private CartItem checkCartItemExistsUnderCart(Long cartId, Long cartItemId) {
        return cartItemRepository.findByIdAndCartId(cartItemId, cartId).orElseThrow(() ->
        {
            log.warn("CartItem {} does not exist in the cart {}", cartItemId, cartId);
            return new ResourceNotFoundException("CartItem " + cartItemId + " does not exist in the cart " + cartId);
        });

    }

    @Transactional
    public void deleteCartItem(Long cartId, Long cartItemId) {
        CartItem cartItem = checkCartItemExistsUnderCart(cartId, cartItemId);
        cartItemRepository.delete(cartItem);
    }
}
