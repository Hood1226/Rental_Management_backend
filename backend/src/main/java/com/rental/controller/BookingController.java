package com.rental.controller;

import com.rental.dto.request.BookingRequest;
import com.rental.dto.response.ApiResponse;
import com.rental.dto.response.BookingResponse;
import com.rental.service.BookingService;
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
@RequestMapping("/bookings")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}, allowCredentials = "true")
@Tag(name = "Bookings", description = "Booking management APIs - CRUD operations for bookings")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {
    
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    
    @Autowired
    private BookingService bookingService;
    
    @GetMapping
    @Operation(summary = "Get all bookings", description = "Retrieve a list of all bookings in the system")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bookings retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getAllBookings() {
        logger.info("GET /bookings - Retrieving all bookings");
        List<BookingResponse> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(ApiResponse.success("Bookings retrieved successfully", bookings));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID", description = "Retrieve a specific booking by its ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Booking retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Booking not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(
            @Parameter(description = "Booking ID", required = true) @PathVariable Integer id) {
        logger.info("GET /bookings/{} - Retrieving booking by ID", id);
        BookingResponse booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(ApiResponse.success("Booking retrieved successfully", booking));
    }
    
    @PostMapping
    @Operation(summary = "Create new booking", description = "Create a new booking (RENT or SALE) for a customer")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Booking created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody BookingRequest request) {
        logger.info("POST /bookings - Creating new booking for customer ID: {}", request.getCustomerId());
        BookingResponse booking = bookingService.createBooking(request);
        return ResponseEntity.ok(ApiResponse.success("Booking created successfully", booking));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update booking", description = "Update an existing booking by its ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Booking updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Booking not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    public ResponseEntity<ApiResponse<BookingResponse>> updateBooking(
            @Parameter(description = "Booking ID", required = true) @PathVariable Integer id,
            @Valid @RequestBody BookingRequest request) {
        logger.info("PUT /bookings/{} - Updating booking", id);
        BookingResponse booking = bookingService.updateBooking(id, request);
        return ResponseEntity.ok(ApiResponse.success("Booking updated successfully", booking));
    }
}


