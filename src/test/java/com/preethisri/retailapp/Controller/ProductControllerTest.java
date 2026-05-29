package com.preethisri.retailapp.Controller;

import com.preethisri.retailapp.DTO.Response.ProductDTOResponse;
import com.preethisri.retailapp.Service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ProductService productService;

    @Test
    void shouldReturnProductForID() throws Exception {
        ProductDTOResponse productDTOResponse = new ProductDTOResponse();
        productDTOResponse.setId(1);

        Mockito.when(productService.getProductByID(1L))
                .thenReturn(productDTOResponse);

        mockMvc.perform(get("/api/products/{id}",1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

}
