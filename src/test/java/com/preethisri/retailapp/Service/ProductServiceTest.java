package com.preethisri.retailapp.Service;

import com.preethisri.retailapp.DTO.Request.ProductDTOPatchRequest;
import com.preethisri.retailapp.DTO.Request.ProductDTORequest;
import com.preethisri.retailapp.DTO.Response.ProductDTOResponse;
import com.preethisri.retailapp.Entity.Product;
import com.preethisri.retailapp.Exception.ProductAlreadyExistsException;
import com.preethisri.retailapp.Exception.ResourceNotFoundException;
import com.preethisri.retailapp.Mapper.ProductMapper;
import com.preethisri.retailapp.Repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @InjectMocks
    ProductService productService;
    @Mock
    ProductRepository productRepository;
    @Mock
    ProductMapper productMapper;

    ProductDTORequest productDTORequest;
    ProductDTOPatchRequest productDTOPatchRequest;
    ProductDTOResponse productDTOResponse;
    Product product;

    @BeforeEach
    void setUp() {
        productDTORequest = new ProductDTORequest();
        productDTORequest.setProductName("Lenovo Yoga");
        productDTORequest.setCategory("Laptop");
        productDTORequest.setColour("Silver");
        productDTORequest.setStorage("512GB");
        productDTORequest.setPrice(BigDecimal.valueOf(1234.43));
        productDTORequest.setStock(3);

        productDTOPatchRequest = new ProductDTOPatchRequest();
        productDTOPatchRequest.setProductName("Lenovo Yoga Slim");
        productDTOPatchRequest.setPrice(BigDecimal.valueOf(2234.43));
        productDTOPatchRequest.setStock(12);

        productDTOResponse = new ProductDTOResponse();
        productDTOResponse.setProductName("Lenovo Yoga");
        productDTOResponse.setCategory("Laptop");
        productDTOResponse.setColour("Silver");
        productDTOResponse.setStorage("512GB");
        productDTOResponse.setPrice(BigDecimal.valueOf(1234.43));
        productDTOResponse.setStock(3);
        productDTOResponse.setId(1L);

        product = new Product();
        product.setProductName("Lenovo Yoga");
        product.setCategory("Laptop");
        product.setColour("Silver");
        product.setStorage("512GB");
        product.setPrice(BigDecimal.valueOf(1234.43));
        product.setStock(3);
        product.setId(1L);
    }

    @Test
    void shouldReturnProductForID() {
        Long id = 1L;

        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(product));
        Mockito.when(productMapper.toDTO(product)).thenReturn(productDTOResponse);
        ProductDTOResponse response = productService.getProductById(1L);
        Assertions.assertEquals(id, response.getId());

        Mockito.verify(productRepository).findById(id);
        Mockito.verify(productMapper).toDTO(product);
    }

    @Test
    void shouldThrowExceptionForInvalidIDForProduct() {
        Long id = 1111L;

        Mockito.when(productRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(id));
        Mockito.verify(productRepository).findById(id);
    }


    @Test
    void shouldReturnProductForCategory() {
        String category = "Laptop";

        Mockito.when(productRepository.findByCategoryContainingIgnoreCase(category)).thenReturn(List.of(product));
        Mockito.when(productMapper.toDTO(product)).thenReturn(productDTOResponse);

        List<ProductDTOResponse> productDTOResponseForCategory = productService.getByCategory(category);
        Assertions.assertEquals(category, productDTOResponseForCategory.get(0).getCategory());

        Mockito.verify(productRepository).findByCategoryContainingIgnoreCase(category);
        Mockito.verify(productMapper).toDTO(product);
    }

    @Test
    void shouldReturnAllProduct() {
        Mockito.when(productRepository.findAll()).thenReturn(List.of(product));
        Mockito.when(productMapper.toDTO(product)).thenReturn(productDTOResponse);

        List<ProductDTOResponse> response = productService.getAllProduct();
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Lenovo Yoga", response.get(0).getProductName());
        Assertions.assertEquals(1L, response.get(0).getId());

        Mockito.verify(productRepository).findAll();
        Mockito.verify(productMapper).toDTO(product);
    }

    @Test
    void shouldReturnEmptyProduct() {
        Mockito.when(productRepository.findAll()).thenReturn(List.of());

        List<ProductDTOResponse> response = productService.getAllProduct();
        Assertions.assertTrue(response.isEmpty());

        Mockito.verify(productRepository).findAll();
    }

    @Test
    void shouldReturnProductByNames() {
        String name1 = "Lenovo";
        String name2 = "Yoga";

        Mockito.when(productRepository.findByProductNameContainingIgnoreCase(name1))
                .thenReturn(List.of(product));
        Mockito.when(productRepository.findByProductNameContainingIgnoreCase(name2))
                .thenReturn(List.of(product));
        Mockito.when(productMapper.toDTO(product)).thenReturn(productDTOResponse);

        List<ProductDTOResponse> response = productService.getProducts(List.of(name1, name2));
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Lenovo Yoga", response.get(0).getProductName());
        Assertions.assertEquals("Laptop", response.get(0).getCategory());

        Mockito.verify(productRepository).findByProductNameContainingIgnoreCase(name1);
        Mockito.verify(productRepository).findByProductNameContainingIgnoreCase(name2);
        Mockito.verify(productMapper).toDTO(product);
    }

    @Test
    void shouldAbleToAddProduct() {
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

        Mockito.verify(productMapper).toEntity(productDTORequest);
        Mockito.verify(productMapper).toDTO(product);
    }

    @Test
    void checkDuplicateProductThrowsException() {
        Mockito.when(productMapper.toEntity(productDTORequest)).thenReturn(product);
        Mockito.when(productRepository.existsByProductNameAndCategoryAndColourAndStorageAndPrice("Lenovo Yoga",
                "Laptop",
                "Silver",
                "512GB",
                BigDecimal.valueOf(1234.43))).thenReturn(true);

        ProductAlreadyExistsException exception = Assertions.assertThrows(ProductAlreadyExistsException.class, () -> productService.addNewProduct(productDTORequest));
        Assertions.assertEquals("Product already exists", exception.getMessage());

        Mockito.verify(productRepository).existsByProductNameAndCategoryAndColourAndStorageAndPrice("Lenovo Yoga",
                "Laptop",
                "Silver",
                "512GB",
                BigDecimal.valueOf(1234.43));
        Mockito.verify(productMapper).toEntity(productDTORequest);
    }

    @Test
    void shouldThrowExceptionWhenDatabaseDuplicateOccurs() {
        Mockito.when(productMapper.toEntity(productDTORequest)).thenReturn(product);
        Mockito.when(productRepository.existsByProductNameAndCategoryAndColourAndStorageAndPrice(any(), any(), any(), any(), any())).thenReturn(false);

        Mockito.when(productRepository.save(product)).thenThrow(DataIntegrityViolationException.class);
        Assertions.assertThrows(ProductAlreadyExistsException.class, () -> productService.addNewProduct(productDTORequest));

        Mockito.verify(productMapper).toEntity(productDTORequest);
        Mockito.verify(productRepository).existsByProductNameAndCategoryAndColourAndStorageAndPrice(any(), any(), any(), any(), any());
        Mockito.verify(productRepository).save(product);
    }

    @Test
    void shouldAbleToUpdateProduct() {
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Mockito.when(productRepository.save(product)).thenReturn(product);
        Mockito.when(productMapper.toDTO(product)).thenReturn(productDTOResponse);

        ProductDTOResponse productUpdated = productService.updateProduct(1L, productDTORequest);
        Assertions.assertEquals("Lenovo Yoga", productUpdated.getProductName());
        Assertions.assertEquals("Laptop", productUpdated.getCategory());
        Assertions.assertEquals("Silver", productUpdated.getColour());
        Assertions.assertEquals("512GB", productUpdated.getStorage());
        Assertions.assertEquals(BigDecimal.valueOf(1234.43), productUpdated.getPrice());
        Assertions.assertEquals(3, productUpdated.getStock());
        Assertions.assertEquals(1L, productUpdated.getId());

        Mockito.verify(productRepository).save(product);
        Mockito.verify(productMapper).toDTO(product);
        Mockito.verify(productRepository).findById(1L);
    }

    @Test
    void shouldThrowErrorForInvalidIdOnUpdate() {
        Mockito.when(productRepository.findById(0L)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(0L, new ProductDTORequest()));
        Mockito.verify(productRepository).findById(0L);
    }

    @Test
    void shouldPartiallyUpdateProductSuccessfully() {
        ProductDTOPatchRequest request = new ProductDTOPatchRequest();
        request.setProductName("Lenovo Yoga Slim");
        request.setPrice(BigDecimal.valueOf(2000));
        request.setStock(10);
        request.setCategory("Ultrabook");
        request.setColour("Black");
        request.setStorage("1TB");
        request.setDescription("Updated Lenovo laptop");

        Mockito.when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));
        Mockito.when(productRepository.save(any(Product.class)))
                .thenReturn(product);
        Mockito.when(productMapper.toDTO(product))
                .thenReturn(productDTOResponse);

        productService.partialUpdateProduct(1L, request);

        Assertions.assertEquals("Lenovo Yoga Slim", product.getProductName());
        Assertions.assertEquals(BigDecimal.valueOf(2000), product.getPrice());
        Assertions.assertEquals(10, product.getStock());
        Assertions.assertEquals("Ultrabook", product.getCategory());
        Assertions.assertEquals("Black", product.getColour());
        Assertions.assertEquals("1TB", product.getStorage());
        Assertions.assertEquals("Updated Lenovo laptop", product.getDescription());


        Mockito.verify(productRepository).save(product);
        Mockito.verify(productMapper).toDTO(product);
    }

    @Test
    void shouldThrowExceptionWhenStockIsNegative() {

        ProductDTOPatchRequest request = new ProductDTOPatchRequest();
        request.setStock(-1);

        Mockito.when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        Assertions.assertThrows(IllegalArgumentException.class, () -> productService.partialUpdateProduct(1L, request));

        Mockito.verify(productRepository, Mockito.never())
                .save(any());
    }

    @Test
    void shouldThrowExceptionWhenProductDoesNotExistForPartialUpdate() {
        Mockito.when(productRepository.findById(100L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.partialUpdateProduct(100L, productDTOPatchRequest));

        Mockito.verify(productRepository, Mockito.never()).save(any());
    }

    @Test
    void partiallyUpdateShouldThrowIllegalArgumentExceptionForInvalidValue() {
        ProductDTOPatchRequest productDTOPatchRequest = new ProductDTOPatchRequest();
        productDTOPatchRequest.setProductName("");

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Assertions.assertThrows(IllegalArgumentException.class, () -> productService.partialUpdateProduct(1L, productDTOPatchRequest));
        Mockito.verify(productRepository).findById(1L);
    }

    @Test
    public void shouldAbleToDeleteProductForValidID() throws Exception {
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);
        Mockito.verify(productRepository).findById(1L);
        Mockito.verify(productRepository).deleteById(1L);
    }

    @Test
    public void shouldThrowErrorForInValidID_deleteProduct() throws Exception {
        Mockito.when(productRepository.findById(123L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(123L));
        Mockito.verify(productRepository, Mockito.never()).deleteById(123L);
    }
}
