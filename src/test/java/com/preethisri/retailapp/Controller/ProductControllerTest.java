package com.preethisri.retailapp.Controller;

import com.preethisri.retailapp.DTO.Request.ProductDTOPatchRequest;
import com.preethisri.retailapp.DTO.Request.ProductDTORequest;
import com.preethisri.retailapp.DTO.Response.ProductDTOResponse;
import com.preethisri.retailapp.Exception.ResourceNotFoundException;
import com.preethisri.retailapp.Service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.MethodArgumentNotValidException;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ProductService productService;
    @Autowired
    ObjectMapper objectMapper;

    ProductDTORequest productDTORequest;
    ProductDTOResponse productDTOResponse;

    @BeforeEach
    void setUp() {
        productDTORequest = new ProductDTORequest();

        productDTORequest.setProductName("Lenovo Yoga");
        productDTORequest.setCategory("Laptop");
        productDTORequest.setColour("Silver");
        productDTORequest.setStorage("512GB");
        productDTORequest.setPrice(BigDecimal.valueOf(1234.43));
        productDTORequest.setStock(3);

        productDTOResponse = new ProductDTOResponse();
        productDTOResponse.setProductName("Lenovo Yoga");
        productDTOResponse.setCategory("Laptop");
        productDTOResponse.setColour("Silver");
        productDTOResponse.setStorage("512GB");
        productDTOResponse.setPrice(BigDecimal.valueOf(1234.43));
        productDTOResponse.setStock(3);
        productDTOResponse.setId(1L);

    }

    @Test
    void shouldReturnProductForID() throws Exception {
        Mockito.when(productService.getProductByID(1L))
                .thenReturn(productDTOResponse);

        mockMvc.perform(get("/api/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldReturnListOfProductForCategory() throws Exception {
        String category = "Laptop";

        Mockito.when(productService.getByCategory(category)).thenReturn(List.of(productDTOResponse));

        mockMvc.perform(get("/api/products/category?name={category}", category))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value(category))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldReturnProductsFilteredByNames() throws Exception {
        String name1 = "iphone";
        String name2 = "Lenova";

        ProductDTOResponse productDTOResponse1 = new ProductDTOResponse();
        ProductDTOResponse productDTOResponse2 = new ProductDTOResponse();

        productDTOResponse1.setProductName("iPhone 17 Pro Max");
        productDTOResponse2.setProductName("Lenovo Yoga");

        List<ProductDTOResponse> productDTOResponseList = List.of(productDTOResponse1, productDTOResponse2);

        Mockito.when(productService.getProductByName(List.of(name1, name2))).thenReturn(productDTOResponseList);

        mockMvc.perform(get("/api/products").param("names", name1, name2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].productName").value("Lenovo Yoga"))
                .andExpect(jsonPath("$[0].productName").value("iPhone 17 Pro Max"));
    }

    @Test
    void shouldReturnAllProductsWhenNoNamesProvided() throws Exception {
        ProductDTOResponse productDTOResponse1 = new ProductDTOResponse();
        ProductDTOResponse productDTOResponse2 = new ProductDTOResponse();

        productDTOResponse1.setProductName("iPhone 17 Pro Max");
        productDTOResponse2.setProductName("Lenovo Yoga");

        List<ProductDTOResponse> productDTOResponseList = List.of(productDTOResponse1, productDTOResponse2);

        Mockito.when(productService.getAllProduct()).thenReturn(productDTOResponseList);
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].productName").value("Lenovo Yoga"))
                .andExpect(jsonPath("$[0].productName").value("iPhone 17 Pro Max"));

    }

    @Test
    void shouldAbleToAddProduct() throws Exception {
        productDTOResponse.setSku("LEN-LAP-AB12");

        Mockito.when(productService.addNewProduct(any(ProductDTORequest.class))).thenReturn(productDTOResponse);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTORequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName").value("Lenovo Yoga"))
                .andExpect(jsonPath("$.category").value("Laptop"))
                .andExpect(jsonPath("$.colour").value("Silver"))
                .andExpect(jsonPath("$.storage").value("512GB"))
                .andExpect(jsonPath("$.price").value(1234.43))
                .andExpect(jsonPath("$.stock").value(3))
                .andExpect(jsonPath("$.sku").value("LEN-LAP-AB12"));
    }

    @Test
    void validateMethodArgumentNotValidaException() throws Exception {
        String request = "{\n" +
                "    \"category\": \"Laptop\",\n" +
                "    \"description\": \"The Lenovo Yoga Slim 7i is a premium thin and light laptop designed for performance, intelligence and mobility\",\n" +
                "    \"price\": 1547.80,\n" +
                "    \"stock\":3,\n" +
                "     \"storage\": \"1TB\",\n" +
                "      \"colour\": \"Black\"\n" +
                "}";

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("productName : Product name is required"));

        Mockito.verify(productService, Mockito.never()).addNewProduct(any(ProductDTORequest.class));
    }

    @Test
    void shouldAbleToUpdateProduct() throws Exception {
        Mockito.when(productService.updateProduct(Mockito.eq(1L), any(ProductDTORequest.class))).thenReturn(productDTOResponse);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTORequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.productName").value("Lenovo Yoga"))
                .andExpect(jsonPath("$.category").value("Laptop"))
                .andExpect(jsonPath("$.colour").value("Silver"))
                .andExpect(jsonPath("$.storage").value("512GB"))
                .andExpect(jsonPath("$.price").value(1234.43))
                .andExpect(jsonPath("$.stock").value(3));
    }

    @Test
    void shouldReturnBadRequestForInvalidID_UpdateProduct() throws Exception {
        mockMvc.perform(put("/api/products/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString((productDTORequest))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestForInvalidContent_UpdateProduct() throws Exception {
        mockMvc.perform(put("/api/products/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductDTORequest())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnResourceNotFoundForNotAvailableID() throws Exception {
        Mockito.when(productService.updateProduct(Mockito.eq(121L), any(ProductDTORequest.class))).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put("/api/products/121")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTORequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAbleToPartiallyUpdateProduct() throws Exception {
        ProductDTOPatchRequest productDTOPatchRequest = new ProductDTOPatchRequest();
        productDTOPatchRequest.setProductName("Lenovo Yoga");

        Mockito.when(productService.partialUpdateProduct(Mockito.eq(1L), any(ProductDTOPatchRequest.class))).thenReturn(productDTOResponse);

        mockMvc.perform(patch("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTOPatchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Lenovo Yoga"));

        Mockito.verify(productService).partialUpdateProduct(Mockito.eq(1L), any(ProductDTOPatchRequest.class));
    }
}


