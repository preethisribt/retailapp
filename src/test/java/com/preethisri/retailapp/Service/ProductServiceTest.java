package com.preethisri.retailapp.Service;

import com.preethisri.retailapp.DTO.Request.ProductDTORequest;
import com.preethisri.retailapp.DTO.Response.ProductDTOResponse;
import com.preethisri.retailapp.Entity.Product;
import com.preethisri.retailapp.Exception.ProductAlreadyExistsException;
import com.preethisri.retailapp.Exception.ResourceNotFoundException;
import com.preethisri.retailapp.Mapper.ProductMapper;
import com.preethisri.retailapp.Repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
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
    void shouldReturnProductForID() {
        Long id = 1L;
        ProductDTOResponse productDTOResponse = new ProductDTOResponse();
        productDTOResponse.setId(id);

        Product product = new Product();
        product.setId(id);

        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(product));
        Mockito.when(productMapper.toDTO(product)).thenReturn(productDTOResponse);
        ProductDTOResponse response = productService.getProductByID(1L);
        Assertions.assertEquals(id, response.getId());
    }

    @Test
    void shouldThrowExceptionForInvalidIDForProduct() {
        Long id = 1111L;
        ProductDTOResponse productDTOResponse = new ProductDTOResponse();
        productDTOResponse.setId(id);

        Mockito.when(productRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.getProductByID(id));
    }


    @Test
    void shouldReturnProductForCategory() {
        String category = "laptop";

        Product product = new Product();
        product.setCategory(category);
        product.setId(5L);
        product.setProductName("Lenovo Yoga");

        ProductDTOResponse productDTOResponse = new ProductDTOResponse();
        productDTOResponse.setCategory(category);
        productDTOResponse.setId(5L);
        productDTOResponse.setProductName("Lenovo Yoga");

        Mockito.when(productRepository.findByCategoryContainingIgnoreCase(category)).thenReturn(List.of(product));
        Mockito.when(productMapper.toDTO(product)).thenReturn(productDTOResponse);

        List<ProductDTOResponse> productDTOResponseForCategory = productService.getByCategory(category);
        Assertions.assertEquals(category, productDTOResponseForCategory.get(0).getCategory());
    }

    @Test
    void shouldReturnAllProduct() {
        Product product = new Product();
        product.setId(10L);
        product.setProductName("iPhone 15 Pro Max");

        ProductDTOResponse productDTOResponse = new ProductDTOResponse();
        productDTOResponse.setId(10L);
        productDTOResponse.setProductName("iPhone 15 Pro Max");

        Mockito.when(productRepository.findAll()).thenReturn(List.of(product));
        Mockito.when(productMapper.toDTO(product)).thenReturn(productDTOResponse);

        List<ProductDTOResponse> response = productService.getAllProduct();
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("iPhone 15 Pro Max", response.get(0).getProductName());
        Assertions.assertEquals(10L, response.get(0).getId());
    }

    @Test
    void shouldReturnEmptyProduct() {
        Mockito.when(productRepository.findAll()).thenReturn(List.of());

        List<ProductDTOResponse> response = productService.getAllProduct();
        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    void shouldReturnProductByNames() {
        String productName = "Canon";

        Product product = new Product();
        product.setProductName("Canon EOS 100D");
        product.setCategory("Camera");

        ProductDTOResponse productDTOResponse = new ProductDTOResponse();
        productDTOResponse.setProductName("Canon EOS 100D");
        productDTOResponse.setCategory("Camera");

        Mockito.when(productRepository.findByProductNameContainingIgnoreCase(productName)).thenReturn(List.of(product));
        Mockito.when(productMapper.toDTO(product)).thenReturn(productDTOResponse);

        List<ProductDTOResponse> response = productService.getProductByName(List.of(productName));
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Canon EOS 100D", response.get(0).getProductName());
        Assertions.assertEquals("Camera", response.get(0).getCategory());
    }

    @Test
    void shouldAbleToAddProduct() {
        ProductDTORequest productDTORequest = new ProductDTORequest();
        productDTORequest.setProductName("Lenovo Yoga");
        productDTORequest.setCategory("Laptop");
        productDTORequest.setColour("Silver");
        productDTORequest.setStorage("512GB");
        productDTORequest.setPrice(BigDecimal.valueOf(1234.43));
        productDTORequest.setStock(3);

        Product product = new Product();
        product.setProductName("Lenovo Yoga");
        product.setCategory("Laptop");

        ProductDTOResponse productDTOResponse = new ProductDTOResponse();
        productDTOResponse.setProductName("Lenovo Yoga");
        productDTOResponse.setCategory("Laptop");
        productDTOResponse.setColour("Silver");
        productDTOResponse.setStorage("512GB");
        productDTOResponse.setPrice(BigDecimal.valueOf(1234.43));
        productDTOResponse.setStock(3);
        productDTOResponse.setId(1L);


        Mockito.when(productMapper.toEntity(productDTORequest)).thenReturn(product);
        Mockito.when(productRepository.save(product)).thenReturn(product);
        Mockito.when(productMapper.toDTO(product)).thenReturn(productDTOResponse);

        ProductDTOResponse response = productService.addNewProduct(productDTORequest);

        Assertions.assertEquals("Lenovo Yoga", response.getProductName());
        Assertions.assertEquals("Laptop", response.getCategory());
        Assertions.assertEquals("Silver", response.getColour());
        Assertions.assertEquals("512GB", response.getStorage());
        Assertions.assertEquals(BigDecimal.valueOf(1234.43), response.getPrice());
        Assertions.assertEquals(3, response.getStock());
        Assertions.assertEquals(1L, response.getId());

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
            Mockito.verify(productRepository).save(captor.capture());

        Assertions.assertTrue(
                captor.getValue().getSku().matches("^LEN-LAP-[A-Z0-9]{4}$"),
                "SKU was: " + captor.getValue().getSku()
        );
    }

    @Test
    void checkDuplicateProductThrowsException() {
        ProductDTORequest productDTORequest = new ProductDTORequest();
        productDTORequest.setProductName("Lenovo Yoga");
        productDTORequest.setCategory("Laptop");
        productDTORequest.setColour("Silver");
        productDTORequest.setStorage("512GB");
        productDTORequest.setPrice(BigDecimal.valueOf(1234.43));

        Product product = new Product();
        product.setProductName("Lenovo Yoga");
        product.setCategory("Laptop");
        product.setColour("Silver");
        product.setStorage("512GB");
        product.setPrice(BigDecimal.valueOf(1234.43));

        Mockito.when(productMapper.toEntity(productDTORequest)).thenReturn(product);
        Mockito.when(productRepository.existsByProductNameAndCategoryAndColourAndStorageAndPrice("Lenovo Yoga",
                "Laptop",
                "Silver",
                "512GB",
                BigDecimal.valueOf(1234.43))).thenReturn(true);

        ProductAlreadyExistsException exception = Assertions.assertThrows(ProductAlreadyExistsException.class, () -> productService.addNewProduct(productDTORequest));
        Assertions.assertEquals("Product already exists", exception.getMessage());
    }
}
