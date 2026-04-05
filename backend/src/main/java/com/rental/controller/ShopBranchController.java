package com.rental.controller;

import com.rental.dto.request.BranchRequest;
import com.rental.dto.request.ShopRequest;
import com.rental.dto.response.ApiResponse;
import com.rental.dto.response.BranchResponse;
import com.rental.dto.response.ShopResponse;
import com.rental.service.ShopBranchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3002"}, allowCredentials = "true")
public class ShopBranchController {

    @Autowired
    private ShopBranchService shopBranchService;

    @GetMapping("/shops")
    public ResponseEntity<ApiResponse<List<ShopResponse>>> getShops() {
        return ResponseEntity.ok(ApiResponse.success("Shops fetched successfully", shopBranchService.getAllShops()));
    }

    @PostMapping("/shops")
    public ResponseEntity<ApiResponse<ShopResponse>> createShop(@Valid @RequestBody ShopRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Shop created successfully", shopBranchService.createShop(request)));
    }

    @GetMapping("/branches")
    public ResponseEntity<ApiResponse<List<BranchResponse>>> getBranches(@RequestParam(required = false) Integer shopId) {
        return ResponseEntity.ok(ApiResponse.success("Branches fetched successfully", shopBranchService.getBranches(shopId)));
    }

    @PostMapping("/branches")
    public ResponseEntity<ApiResponse<BranchResponse>> createBranch(@Valid @RequestBody BranchRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Branch created successfully", shopBranchService.createBranch(request)));
    }
}
