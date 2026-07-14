package com.preethisri.retailapp.Controller;

import com.preethisri.retailapp.DTO.Request.ProductDTOPatchRequest;
import com.preethisri.retailapp.DTO.Request.ProductDTORequest;
import com.preethisri.retailapp.DTO.Response.ProductDTOResponse;
import com.preethisri.retailapp.Service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Products",
        description = "APIs for managing retail products"
)
public class ProductController {

    private final ProductService productService;

    @Operation(
            summary = "Get all products",
            description = "Retrieves all products from the retail inventory. "
                    + "Optionally filters products by product names using the names parameter."
    )
    @GetMapping
    public ResponseEntity<List<ProductDTOResponse>> getAllProductByName(@RequestParam(required = false) List<String> names) {
        if (names != null && !names.isEmpty()) {
            return ResponseEntity.ok(productService.getProducts(names));
        } else
            return ResponseEntity.ok(productService.getAllProduct());
    }

    @Operation(
            summary = "Get product by ID",
            description = "Retrieves a single product from the inventory using its unique product ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTOResponse> getProductByID(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(
            summary = "Get products by category",
            description = "Retrieves all products belonging to a specific category."
    )
    @GetMapping("/category")
    public ResponseEntity<List<ProductDTOResponse>> getProductByCategory(@RequestParam String name) {
        return ResponseEntity.ok(productService.getByCategory(name));
    }

    @Operation(
            summary = "Create a new product",
            description = "Creates a new product and adds it to the retail inventory."
    )
    @PostMapping
    public ResponseEntity<ProductDTOResponse> addNewProduct(@Valid @RequestBody ProductDTORequest data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addNewProduct(data));
    }

    @Operation(
            summary = "Update product",
            description = "Updates all details of an existing product using its ID."
    )
    @PutMapping(value = "/{id}")
    public ResponseEntity<ProductDTOResponse> updateProduct(@PathVariable @Min(1) long id, @Valid @RequestBody ProductDTORequest data) {
        return ResponseEntity.ok(productService.updateProduct(id, data));
    }

    @Operation(
            summary = "Partially update product",
            description = "Updates selected fields of an existing product without replacing the complete product."
    )
    @PatchMapping("/{id}")
    public ResponseEntity<ProductDTOResponse> partialUpdateProduct(@PathVariable @Min(1) long id, @RequestBody @Valid ProductDTOPatchRequest data) {
        return ResponseEntity.ok(productService.partialUpdateProduct(id, data));
    }

    @Operation(
            summary = "Delete product",
            description = "Deletes a product from the retail inventory using its ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable @Min(1) long id) {
        productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
