package com.rental.controller;

import com.rental.dto.request.CustomerRequest;
import com.rental.dto.response.ApiResponse;
import com.rental.dto.response.CustomerResponse;
import com.rental.service.CustomerService;
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
@RequestMapping("/customers")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}, allowCredentials = "true")
@Tag(name = "Customers", description = "Customer management APIs - CRUD operations for customers")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    
    @Autowired
    private CustomerService customerService;
    
    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieve a list of all customers in the system")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Customers retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAllCustomers() {
        logger.info("GET /customers - Retrieving all customers");
        List<CustomerResponse> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(ApiResponse.success("Customers retrieved successfully", customers));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Retrieve a specific customer by its ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Customer retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(
            @Parameter(description = "Customer ID", required = true) @PathVariable Integer id) {
        logger.info("GET /customers/{} - Retrieving customer by ID", id);
        CustomerResponse customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(ApiResponse.success("Customer retrieved successfully", customer));
    }
    
    @PostMapping
    @Operation(summary = "Create new customer", description = "Create a new customer in the system")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Customer created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
            @Valid @RequestBody CustomerRequest request) {
        logger.info("POST /customers - Creating new customer: {}", request.getCustomerName());
        CustomerResponse customer = customerService.createCustomer(request);
        return ResponseEntity.ok(ApiResponse.success("Customer created successfully", customer));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update customer", description = "Update an existing customer by its ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Customer updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
            @Parameter(description = "Customer ID", required = true) @PathVariable Integer id,
            @Valid @RequestBody CustomerRequest request) {
        logger.info("PUT /customers/{} - Updating customer", id);
        CustomerResponse customer = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(ApiResponse.success("Customer updated successfully", customer));
    }
}


