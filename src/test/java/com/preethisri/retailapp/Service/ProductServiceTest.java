package com.preethisri.retailapp.Service;

import com.preethisri.retailapp.DTO.Response.ProductDTOResponse;
import com.preethisri.retailapp.Entity.Product;
import com.preethisri.retailapp.Exception.ResourceNotFoundException;
import com.preethisri.retailapp.Mapper.ProductMapper;
import com.preethisri.retailapp.Repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @InjectMocks
    ProductService productService;
    @Mock
    ProductRepository productRepository;
    @Mock
    ProductMapper productMapper;

    @Test
    void shouldReturnProduct() {
        Long id = 1L;
        ProductDTOResponse productDTOResponse = new ProductDTOResponse();
        productDTOResponse.setId(id);

        Product product = new Product();
        product.setId(id);

        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(product));
        Mockito.when(productMapper.toDTO(product)).thenReturn(productDTOResponse);
        ProductDTOResponse response = productService.getProductByID(1L);
        Assertions.assertEquals(id,response.getId());
    }

    @Test
    void shouldThrowExceptionProduct() {
        Long id = 1111L;
        ProductDTOResponse productDTOResponse = new ProductDTOResponse();
        productDTOResponse.setId(id);

        Mockito.when(productRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.getProductByID(id));
    }
}
