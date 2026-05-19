package com.preethisri.retailapp.Service;

import com.preethisri.retailapp.Entity.Product;
import com.preethisri.retailapp.Exception.ResourceNotFoundException;
import com.preethisri.retailapp.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    public ResponseEntity<List<Product>> getAllProduct() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    public Product getProductByID(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for the id " + id));
    }

}
