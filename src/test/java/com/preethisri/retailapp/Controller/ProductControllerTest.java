package com.preethisri.retailapp.Controller;

import com.preethisri.retailapp.DTO.Request.Product.ProductDTOPatchRequest;
import com.preethisri.retailapp.DTO.Request.Product.ProductDTORequest;
import com.preethisri.retailapp.DTO.Response.Product.ProductDTOResponse;
import com.preethisri.retailapp.Exception.ProductAlreadyExistsException;
import com.preethisri.retailapp.Exception.ResourceNotFoundException;
import com.preethisri.retailapp.Service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

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
    void shouldReturnResourceNotFoundForId() throws Exception {
        Mockito.when(productService.getProductById(1L))
                .thenThrow(new ResourceNotFoundException("Product not found for the id"));

        mockMvc.perform(get("/api/products/{id}", 1L))
                .andExpect(status().isNotFound());

        Mockito.verify(productService).getProductById(1L);
    }

    @Test
    void shouldReturnProductForId() throws Exception {
        Mockito.when(productService.getProductById(1L))
                .thenReturn(productDTOResponse);

        mockMvc.perform(get("/api/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        Mockito.verify(productService).getProductById(1L);
    }

    @Test
    void shouldReturnListOfProductForCategory() throws Exception {
        String category = "Laptop";

        Mockito.when(productService.getByCategory(category))
                .thenReturn(List.of(productDTOResponse));

        mockMvc.perform(get("/api/products/category?name={category}", category))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value(category))
                .andExpect(jsonPath("$.length()").value(1));

        Mockito.verify(productService).getByCategory(category);
    }

    @Test
    void shouldReturnProductsFilteredByNames() throws Exception {
        String name1 = "iphone";
        String name2 = "Lenovo";

        ProductDTOResponse productDTOResponse1 = new ProductDTOResponse();
        ProductDTOResponse productDTOResponse2 = new ProductDTOResponse();

        productDTOResponse1.setProductName("iPhone 17 Pro Max");
        productDTOResponse2.setProductName("Lenovo Yoga");

       List.of(productDTOResponse1, productDTOResponse2);

        mockMvc.perform(get("/api/products").param("names", name1, name2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].productName").value("Lenovo Yoga"))
                .andExpect(jsonPath("$[0].productName").value("iPhone 17 Pro Max"));

        Mockito.verify(productService).getProducts(List.of(name1, name2));
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

        Mockito.verify(productService).getAllProduct();
    }

    @Test
    void shouldAddProductSuccessfully() throws Exception {
        productDTOResponse.setSku("LEN-LAP-AB12");

        Mockito.when(productService.addNewProduct(any(ProductDTORequest.class)))
                .thenReturn(productDTOResponse);

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

        Mockito.verify(productService).addNewProduct(any(ProductDTORequest.class));
    }

    @Test
    void shouldReturnBadRequestWhenRequestValidationFails() throws Exception {
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
    void shouldReturnConflictWhenProductAlreadyExists() throws Exception {
        Mockito.when(productService.addNewProduct(any(ProductDTORequest.class)))
                .thenThrow(new ProductAlreadyExistsException("Product already exists"));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTORequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        Mockito.when(productService.updateProduct(Mockito.eq(1L), any(ProductDTORequest.class)))
                .thenReturn(productDTOResponse);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTORequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productName").value("Lenovo Yoga"))
                .andExpect(jsonPath("$.category").value("Laptop"))
                .andExpect(jsonPath("$.colour").value("Silver"))
                .andExpect(jsonPath("$.storage").value("512GB"))
                .andExpect(jsonPath("$.price").value(1234.43))
                .andExpect(jsonPath("$.stock").value(3));

        Mockito.verify(productService).updateProduct(Mockito.eq(1L), any(ProductDTORequest.class));
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
    void shouldReturnResourceNotFound_UpdateProduct() throws Exception {
        Mockito.when(productService.updateProduct(Mockito.eq(121L), any(ProductDTORequest.class))).thenThrow(new ResourceNotFoundException("Product not found"));

        mockMvc.perform(put("/api/products/121")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTORequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateProductSuccessfully_PartialUpdate() throws Exception {
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

    @Test
    void shouldReturnBadRequestForInvalidProduct_PartialUpdate() throws Exception {
        ProductDTOPatchRequest productDTOPatchRequest = new ProductDTOPatchRequest();
        productDTOPatchRequest.setProductName("");

        Mockito.when(productService.partialUpdateProduct(Mockito.eq(1L), any(ProductDTOPatchRequest.class)))
                .thenThrow(new IllegalArgumentException("Product name can't be blank"));

        mockMvc.perform(patch("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTOPatchRequest)))
                .andExpect(status().isBadRequest());

        Mockito.verify(productService).partialUpdateProduct(Mockito.eq(1L), any(ProductDTOPatchRequest.class));
    }

    @Test
    public void shouldDeleteProduct() throws Exception {
        Mockito.doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(productService).deleteProduct(1L);
    }

    @Test
    public void shouldReturnResourceNotFound_DeleteProduct() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Product not found for the id")).when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found for the id"));

        Mockito.verify(productService).deleteProduct(1L);
    }
}


