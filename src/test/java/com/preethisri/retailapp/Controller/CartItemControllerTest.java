package com.preethisri.retailapp.Controller;

import com.preethisri.retailapp.DTO.Request.CartItem.CartItemDTORequest;
import com.preethisri.retailapp.DTO.Response.CartItem.CartItemDTOResponse;
import com.preethisri.retailapp.Exception.InsufficientStockException;
import com.preethisri.retailapp.Exception.ProductAlreadyInCartException;
import com.preethisri.retailapp.Exception.ResourceNotFoundException;
import com.preethisri.retailapp.Service.CartItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartItemController.class)
public class CartItemControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    CartItemService cartItemService;

    CartItemDTORequest cartItemDTORequest;
    CartItemDTOResponse cartItemDTOResponse;

    @BeforeEach
    void setUp() {
        cartItemDTORequest = new CartItemDTORequest();
        cartItemDTORequest.setProductId(6L);
        cartItemDTORequest.setQuantity(1);

        cartItemDTOResponse = new CartItemDTOResponse();
        cartItemDTOResponse.setAmount(BigDecimal.valueOf(2400.92));
        cartItemDTOResponse.setCartId(1L);
        cartItemDTOResponse.setCartItemId(1L);
        cartItemDTOResponse.setProductId(6L);
        cartItemDTOResponse.setProductName("iPhone 18 Pro Max");
        cartItemDTOResponse.setQuantity(1);
        cartItemDTOResponse.setUnitPrice(BigDecimal.valueOf(2400.92));
    }

    @Test
    void shouldAbleToAddCartItem() throws Exception {
        Long cartId = 1L;
        when(cartItemService.addProductToCart(eq(cartId), any(CartItemDTORequest.class)))
                .thenReturn(cartItemDTOResponse);

        mockMvc.perform(post("/api/carts/{cartId}/items", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemDTORequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(2400.92))
                .andExpect(jsonPath("$.cartId").value(cartId))
                .andExpect(jsonPath("$.cartItemId").value(1L))
                .andExpect(jsonPath("$.productId").value(6L))
                .andExpect(jsonPath("$.productName").value("iPhone 18 Pro Max"))
                .andExpect(jsonPath("$.quantity").value(1))
                .andExpect(jsonPath("$.unitPrice").value(2400.92));

        verify(cartItemService).addProductToCart(eq(cartId), any(CartItemDTORequest.class));
    }

    @Test
    void shouldNotAbleToAddCartItem_CartId_Unavailable() throws Exception {
        Long cartId = 100L;
        when(cartItemService.addProductToCart(eq(cartId), any(CartItemDTORequest.class)))
                .thenThrow(new ResourceNotFoundException("Cart not found: " + cartId));

        mockMvc.perform(post("/api/carts/{cartId}/items", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemDTORequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Cart not found: " + cartId));

        verify(cartItemService).addProductToCart(eq(cartId), any(CartItemDTORequest.class));
    }

    @ParameterizedTest
    @ValueSource(longs = {-100L, 0})
    void shouldNotAbleToAddCartItem_CartId_invalid(Long cartId) throws Exception {
        mockMvc.perform(post("/api/carts/{cartId}/items", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemDTORequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("cartId must be greater than 0"));

        verifyNoInteractions(cartItemService);
    }

    @Test
    void shouldNotAbleToAddCartItem_duplicateProduct() throws Exception {
        Long cartId = 1L;
        Long productID = 6L;

        when(cartItemService.addProductToCart(eq(cartId), any(CartItemDTORequest.class)))
                .thenThrow(new ProductAlreadyInCartException("Product ID already exists in the cart: " +productID));

        mockMvc.perform(post("/api/carts/{cartId}/items", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemDTORequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Product ID already exists in the cart: " + productID));

        verify(cartItemService).addProductToCart(eq(cartId), any(CartItemDTORequest.class));
    }

    @Test
    void shouldNotAbleToAddCartItem_unAvailableProduct() throws Exception {
        Long cartId = 1L;
        Long productId = 200L;
        cartItemDTORequest.setProductId(productId);

        when(cartItemService.addProductToCart(eq(cartId), any(CartItemDTORequest.class)))
                .thenThrow(new ResourceNotFoundException("Product not found: " + productId));

        mockMvc.perform(post("/api/carts/{cartId}/items", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemDTORequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product not found: " + productId));

        verify(cartItemService).addProductToCart(eq(cartId), any(CartItemDTORequest.class));
    }

    @Test
    void shouldNotAbleToAddCartItem_InsufficientStock() throws Exception {
        Long cartId = 1L;
        when(cartItemService.addProductToCart(eq(cartId), any(CartItemDTORequest.class)))
                .thenThrow(new InsufficientStockException("Insufficient stock for product"));

        mockMvc.perform(post("/api/carts/{cartId}/items", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemDTORequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Insufficient stock for product"));

        verify(cartItemService).addProductToCart(eq(cartId), any(CartItemDTORequest.class));
    }

    @Test
    void shouldNotAbleToAddCartItem_malformedJSON() throws Exception {
        Long cartId = 1L;
        String request = "{\n" +
                "  \"productId\": 123230,\n" +
                "  \"quantity\": 2\n" +
                "\n";

        mockMvc.perform(post("/api/carts/{cartId}/items", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid request body"));

        verifyNoInteractions(cartItemService);
    }

    @Test
    void shouldNotAbleToAddCartItem_invalidRequestBody() throws Exception {
        invalidRequestBodyTest(-10L, 20, "Product ID must be greater than 0");
        invalidRequestBodyTest(0L, 20, "Product ID must be greater than 0");
        invalidRequestBodyTest(null, 20, "Product ID can't be empty");
        invalidRequestBodyTest(57L, -10, "Quantity must be greater than 0");
        invalidRequestBodyTest(57L, 0, "Quantity must be greater than 0");
        invalidRequestBodyTest(57L, null, "Quantity can't be empty");
    }

    private void invalidRequestBodyTest(Long productID, Integer quantity, String expectedMessage) throws Exception {
        Long cartId = 1L;

        CartItemDTORequest request = new CartItemDTORequest();
        request.setProductId(productID);
        request.setQuantity(quantity);


        mockMvc.perform(post("/api/carts/{cartId}/items", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(expectedMessage));

        verifyNoInteractions(cartItemService);
    }

    @Test
    void shouldNotAbleToAddCartItem_emptyRequestBody() throws Exception {
        Long cartId = 1L;

        mockMvc.perform(post("/api/carts/{cartId}/items", cartId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid request body"));

        verifyNoInteractions(cartItemService);
    }
}
