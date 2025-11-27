package com.example.product.controller.product;

import com.example.product.dto.ProductRequestDto;
import com.example.product.model.Product;
import com.example.product.response.ApiResponse;
import com.example.product.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product CRUD API")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getProductList() {
        List<Product> products = productService.getProductList();

        if (products == null || products.isEmpty()) {
            return createResponse(true, HttpStatus.NO_CONTENT, "No products found", null);
        }

        return createResponse(true, HttpStatus.OK, "Product list retrieved successfully", products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);

        if (product == null) {
            return createResponse(false, HttpStatus.NOT_FOUND, "Product not found", null);
        }

        return createResponse(true, HttpStatus.OK, "Product retrieved successfully", product);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(@Valid @RequestBody ProductRequestDto dto) {
        try {
            // Check if product name already exists
            if (productService.existsByName(dto.getName())) {
                return createResponse(
                        false,
                        HttpStatus.CONFLICT,
                        "Product with same name already exists",
                        null
                );
            }

            Product savedProduct = productService.createProduct(dto);
            return createResponse(true, HttpStatus.CREATED, "Product created successfully", savedProduct);
        } catch (Exception e) {
            return createResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to create product: " + e.getMessage(), null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable Long id,
                                                              @Valid @RequestBody ProductRequestDto dto) {
        Product updatedProduct = productService.updateProduct(id, dto);

        if (updatedProduct == null) {
            return createResponse(false, HttpStatus.NOT_FOUND, "Product not found", null);
        }

        return createResponse(true, HttpStatus.OK, "Product updated successfully", updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);

        if (!deleted) {
            return createResponse(false, HttpStatus.NOT_FOUND, "Product not found", null);
        }

        return createResponse(true, HttpStatus.NO_CONTENT, "Product deleted successfully", null);
    }

    private <T> ResponseEntity<ApiResponse<T>> createResponse(boolean success, HttpStatus status,
                                                              String message, T data) {
        return new ResponseEntity<>(
                new ApiResponse<>(success, status.value(), message, data),
                status);
    }
}