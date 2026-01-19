package com.rental.controller;

import com.rental.dto.response.ApiResponse;
import com.rental.entity.ProductSize;
import com.rental.repository.ProductSizeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sizes")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}, allowCredentials = "true")
@Tag(name = "Product Sizes", description = "Product size management APIs")
@SecurityRequirement(name = "bearerAuth")
public class ProductSizeController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductSizeController.class);
    
    @Autowired
    private ProductSizeRepository sizeRepository;
    
    @GetMapping
    @Operation(summary = "Get all product sizes", description = "Retrieve a list of all available product sizes")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sizes retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    public ResponseEntity<ApiResponse<List<ProductSize>>> getAllSizes() {
        logger.info("GET /sizes - Retrieving all product sizes");
        List<ProductSize> sizes = sizeRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("Sizes retrieved successfully", sizes));
    }
}

