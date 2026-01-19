package com.rental.controller;

import com.rental.dto.request.ProductRequest;
import com.rental.dto.response.ApiResponse;
import com.rental.dto.response.ProductResponse;
import com.rental.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}, allowCredentials = "true")
@Tag(name = "Products", description = "Product management APIs - CRUD operations for products")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve a list of all products in the system")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Products retrieved successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        logger.info("GET /products - Retrieving all products");
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", products));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a specific product by its ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @Parameter(description = "Product ID", required = true) @PathVariable Integer id) {
        logger.info("GET /products/{} - Retrieving product by ID", id);
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success("Product retrieved successfully", product));
    }
    
    @PostMapping
    @Operation(summary = "Create new product", description = "Create a new product in the system")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request) {
        logger.info("POST /products - Creating new product: {}", request.getProductName());
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity.ok(ApiResponse.success("Product created successfully", product));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update an existing product by its ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable Integer id,
            @Valid @RequestBody ProductRequest request) {
        logger.info("PUT /products/{} - Updating product", id);
        ProductResponse product = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", product));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Soft delete a product by setting isActive to false")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    public ResponseEntity<ApiResponse<String>> deleteProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable Integer id) {
        logger.info("DELETE /products/{} - Deleting product", id);
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }
}


