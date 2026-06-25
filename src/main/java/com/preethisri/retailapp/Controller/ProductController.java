package com.preethisri.retailapp.Controller;

import com.preethisri.retailapp.DTO.Request.ProductDTOPatchRequest;
import com.preethisri.retailapp.DTO.Request.ProductDTORequest;
import com.preethisri.retailapp.DTO.Response.ProductDTOResponse;
import com.preethisri.retailapp.Entity.Product;
import com.preethisri.retailapp.Service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDTOResponse>> getAllProductByName(@RequestParam(required = false) List<String> names) {
        if (names != null && !names.isEmpty()) {
            List<ProductDTOResponse> products = productService.getProductByName(names);
            return ResponseEntity.ok(products);
        } else
            return ResponseEntity.ok(productService.getAllProduct());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTOResponse> getProductByID(@PathVariable @Min(1) Long id) {
        ProductDTOResponse product = productService.getProductByID(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/category")
    public ResponseEntity<List<ProductDTOResponse>> getProductByCategory(@RequestParam String name) {
        List<ProductDTOResponse> products = productService.getByCategory(name);
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<ProductDTOResponse> addNewProduct(@Valid @RequestBody ProductDTORequest data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addNewProduct(data));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ProductDTOResponse> updateProduct(@PathVariable @Min(1) long id, @Valid @RequestBody ProductDTORequest data) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.updateProduct(id, data));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductDTOResponse> partialUpdateProduct(@PathVariable @Min(1) long id, @RequestBody @Valid ProductDTOPatchRequest data) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.partialUpdateProduct(id, data));
    }
}
