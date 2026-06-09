package com.preethisri.retailapp.Service;

import com.preethisri.retailapp.Exception.ProductAlreadyExistsException;
import com.preethisri.retailapp.Mapper.ProductMapper;
import com.preethisri.retailapp.DTO.Request.ProductDTORequest;
import com.preethisri.retailapp.DTO.Response.ProductDTOResponse;
import com.preethisri.retailapp.Entity.Product;
import com.preethisri.retailapp.Exception.ResourceNotFoundException;
import com.preethisri.retailapp.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public List<ProductDTOResponse> getAllProduct() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(productMapper::toDTO).toList();
    }

    public ProductDTOResponse getProductByID(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for the id " + id));

        return productMapper.toDTO(product);
    }

    public List<ProductDTOResponse> getProductByName(List<String> names) {
        List<Product> result = new ArrayList<>();

        for (String name : names) {
            result.addAll(productRepository.findByProductNameContainingIgnoreCase(name));
        }
        return result.stream().map(productMapper::toDTO).toList();
    }

    public List<ProductDTOResponse> getByCategory(String name) {
        List<Product> products = productRepository.findByCategoryContainingIgnoreCase(name);
        return products.stream().map(productMapper::toDTO).toList();
    }

    public ProductDTOResponse addNewProduct(ProductDTORequest data) {
        Product entity = productMapper.toEntity(data);

        if (checkDuplicateProduct(entity))
            throw new ProductAlreadyExistsException("Product already exists");
        else {
            entity.setSku(generateSKU(entity));
            Product product = productRepository.save(entity);
            return productMapper.toDTO(product);
        }
    }

    public boolean checkDuplicateProduct(Product product) {
        return productRepository.existsByProductNameAndCategoryAndColourAndStorageAndPrice(product.getProductName(),
                product.getCategory(),
                product.getColour(),
                product.getStorage(),
                product.getPrice());
    }

    public String generateSKU(Product product) {
        String productName = product.getProductName().substring(0, Math.min(3, product.getProductName().length())).toUpperCase();
        String category = product.getCategory().substring(0, Math.min(3, product.getCategory().length())).toUpperCase();
        String suffix = UUID.randomUUID().toString()
                .substring(0, 4)
                .toUpperCase();

        return productName + "-" + category + "-" + suffix;
    }
}
