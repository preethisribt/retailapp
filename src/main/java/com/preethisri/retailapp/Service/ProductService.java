package com.preethisri.retailapp.Service;

import com.preethisri.retailapp.DTO.Request.Product.ProductDTOPatchRequest;
import com.preethisri.retailapp.DTO.Request.Product.ProductDTORequest;
import com.preethisri.retailapp.DTO.Response.Product.ProductDTOResponse;
import com.preethisri.retailapp.Entity.Product;
import com.preethisri.retailapp.Exception.ResourceAlreadyExistsException;
import com.preethisri.retailapp.Exception.ResourceNotFoundException;
import com.preethisri.retailapp.Mapper.ProductMapper;
import com.preethisri.retailapp.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public List<ProductDTOResponse> getAllProduct() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(productMapper::toDTO).toList();
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product with id {} not found", id);
                    return new ResourceNotFoundException("Product not found for the id " + id);
                });
    }

    @Transactional(readOnly = true)
    public ProductDTOResponse getProductById(Long id) {
        Product product = findProductById(id);
        return productMapper.toDTO(product);
    }

    @Transactional(readOnly = true)
    public List<ProductDTOResponse> getProducts(List<String> names) {
        Set<Product> result = new LinkedHashSet<>();

        for (String name : names) {
            result.addAll(productRepository.findByProductNameContainingIgnoreCase(name));
        }
        return result.stream().map(productMapper::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductDTOResponse> getByCategory(String name) {
        List<Product> products = productRepository.findByCategoryContainingIgnoreCase(name);
        return products.stream().map(productMapper::toDTO).toList();
    }

    @Transactional
    public ProductDTOResponse addNewProduct(ProductDTORequest request) {
        Product entity = productMapper.toEntity(request);

        if (checkDuplicateProduct(entity)) {
            log.warn("Duplicate product found {}", entity.getProductName());
            throw new ResourceAlreadyExistsException("Product already exists");
        }

        log.info("Creating Product {}", entity.getProductName());
        entity.setSku(generateSKU(entity));

        try {
            Product product = productRepository.save(entity);
            log.debug("Product created successfully with id {}", product.getId());

            return productMapper.toDTO(product);
        } catch (DataIntegrityViolationException exception) {
            log.error("Failed to create product {} due to database constraint violation", entity.getProductName());
            throw new ResourceAlreadyExistsException("Product already exists");
        }
    }

    private boolean checkDuplicateProduct(Product product) {
        return productRepository.existsByProductNameAndCategoryAndColourAndStorageAndPrice(product.getProductName(),
                product.getCategory(),
                product.getColour(),
                product.getStorage(),
                product.getPrice());
    }

    private String generateSKU(Product product) {
        String productName = product.getProductName().substring(0, Math.min(3, product.getProductName().length())).toUpperCase();
        String category = product.getCategory().substring(0, Math.min(3, product.getCategory().length())).toUpperCase();
        String suffix = UUID.randomUUID().toString()
                .substring(0, 4)
                .toUpperCase();

        return productName + "-" + category + "-" + suffix;
    }

    @Transactional
    public ProductDTOResponse updateProduct(long id, ProductDTORequest request) {
        Product existingProduct = findProductById(id);
        log.info("Updating product with id {}", id);

        existingProduct.setProductName(request.getProductName());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setStock(request.getStock());
        existingProduct.setCategory(request.getCategory());
        existingProduct.setColour(request.getColour());
        existingProduct.setStorage(request.getStorage());

        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Product {} updated successfully", id);

        return productMapper.toDTO(updatedProduct);
    }

    @Transactional
    public ProductDTOResponse partialUpdateProduct(long id, ProductDTOPatchRequest request) {
        Product existingProduct = findProductById(id);
        log.info("Updating product with id {}", id);

        updateProductName(existingProduct, id, request);
        updatePrice(existingProduct, id, request);
        updateStock(existingProduct, id, request);
        updateCategory(existingProduct, id, request);
        updateColour(existingProduct, id, request);
        updateStorage(existingProduct, id, request);
        updateDescription(existingProduct, id, request);


        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Product {} updated successfully", id);

        return productMapper.toDTO(updatedProduct);
    }

    private void updateProductName(Product existingProduct, long id, ProductDTOPatchRequest request) {
        if (request.getProductName() != null) {
            if (request.getProductName().isBlank()) {
                log.warn("Product name is blank for {}", id);
                throw new IllegalArgumentException("Product name can't be blank");
            }

            existingProduct.setProductName(request.getProductName());
            log.debug("ProductName {} updated successfully for {}", request.getProductName(), id);
        }
    }

    private void updatePrice(Product existingProduct, long id, ProductDTOPatchRequest request) {
        if (request.getPrice() != null) {
            if (request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("Price is 0 or negative for {}", id);
                throw new IllegalArgumentException("Price must be greater than 0");
            }

            existingProduct.setPrice(request.getPrice());
            log.debug("Price {} updated successfully for {}", request.getPrice(), id);
        }
    }


    private void updateStock(Product existingProduct, long id, ProductDTOPatchRequest request) {
        if (request.getStock() != null) {
            if (request.getStock() < 0) {
                log.warn("Stock is negative for {}", id);
                throw new IllegalArgumentException("Stock can't be negative");
            }

            existingProduct.setStock(request.getStock());
            log.debug("Stock {} updated successfully for product {}", request.getStock(), id);
        }
    }

    private void updateCategory(Product existingProduct, long id, ProductDTOPatchRequest request) {
        if (request.getCategory() != null) {
            if (request.getCategory().isBlank()) {
                log.warn("Category is blank for {}", id);
                throw new IllegalArgumentException("Category can't be blank");
            }
            existingProduct.setCategory(request.getCategory());
            log.debug("Category {} updated successfully for product {}", request.getCategory(), id);
        }
    }

    private void updateColour(Product existingProduct, long id, ProductDTOPatchRequest request) {
        if (request.getColour() != null) {
            if (request.getColour().isBlank()) {
                log.warn("Colour is blank for {}", id);
                throw new IllegalArgumentException("Colour can't be blank");
            }

            existingProduct.setColour(request.getColour());
            log.debug("Colour {} updated successfully for {}", request.getColour(), id);
        }
    }

    private void updateDescription(Product existingProduct, long id, ProductDTOPatchRequest request) {
        if (request.getDescription() != null) {
            if (request.getDescription().isBlank()) {
                log.warn("Description is blank for product {}", id);
                throw new IllegalArgumentException("description can't be blank");
            }
            existingProduct.setDescription(request.getDescription());
            log.debug("Description {} updated successfully for {}", request.getDescription(), id);
        }
    }

    private void updateStorage(Product existingProduct, long id, ProductDTOPatchRequest request) {
        if (request.getStorage() != null) {
            if (request.getStorage().isBlank()) {
                log.warn("Storage is blank for {}", id);
                throw new IllegalArgumentException("Storage can't be blank");
            }

            existingProduct.setStorage(request.getStorage());
            log.debug("Storage {} updated successfully for {}", request.getStorage(), id);
        }
    }

    @Transactional
    public void deleteProduct(long id) {
        findProductById(id);
        productRepository.deleteById(id);
        log.info("Product {} deleted successfully", id);
    }
}
