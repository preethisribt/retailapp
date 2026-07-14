package com.preethisri.retailapp.Controller;

import com.preethisri.retailapp.DTO.Request.ProductDTOPatchRequest;
import com.preethisri.retailapp.DTO.Request.ProductDTORequest;
import com.preethisri.retailapp.DTO.Response.ProductDTOResponse;
import com.preethisri.retailapp.Service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDTOResponse>> getAllProductByName(@RequestParam(required = false) List<String> names) {
        if (names != null && !names.isEmpty()) {
            return ResponseEntity.ok(productService.getProducts(names));
        } else
            return ResponseEntity.ok(productService.getAllProduct());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTOResponse> getProductByID(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/category")
    public ResponseEntity<List<ProductDTOResponse>> getProductByCategory(@RequestParam String name) {
        return ResponseEntity.ok(productService.getByCategory(name));
    }

    @PostMapping
    public ResponseEntity<ProductDTOResponse> addNewProduct(@Valid @RequestBody ProductDTORequest data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addNewProduct(data));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ProductDTOResponse> updateProduct(@PathVariable @Min(1) long id, @Valid @RequestBody ProductDTORequest data) {
        return ResponseEntity.ok(productService.updateProduct(id, data));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductDTOResponse> partialUpdateProduct(@PathVariable @Min(1) long id, @RequestBody @Valid ProductDTOPatchRequest data) {
        return ResponseEntity.ok(productService.partialUpdateProduct(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable @Min(1) long id) {
        productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
