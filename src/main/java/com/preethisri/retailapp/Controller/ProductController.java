package com.preethisri.retailapp.Controller;

import com.preethisri.retailapp.Entity.Product;
import com.preethisri.retailapp.Service.ProductService;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("api/products")
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProductByName(@RequestParam(required = false) List<String> names) {
        if (names != null) {
            List<Product> products = productService.getProductByName(names);
            return ResponseEntity.ok(products);
        }
        return productService.getAllProduct();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductByID(@PathVariable @Min(1) Long id) {
        Product product = productService.getProductByID(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/category")
    public ResponseEntity<List<Product>> getProductByCategory(@RequestParam String name) {
        List<Product> products = productService.getByCategory(name);
        return ResponseEntity.ok(products);
    }
}
