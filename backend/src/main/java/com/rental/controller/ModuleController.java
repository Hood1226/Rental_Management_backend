package com.rental.controller;

import com.rental.dto.request.ModuleRequest;
import com.rental.dto.response.ApiResponse;
import com.rental.dto.response.ModuleResponse;
import com.rental.service.ModuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/modules")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3002"}, allowCredentials = "true")
@RequiredArgsConstructor
@Tag(name = "Module Management", description = "APIs for managing system modules")
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    @GetMapping
    @Operation(summary = "Get all modules")
    public ResponseEntity<ApiResponse<List<ModuleResponse>>> getAllModules() {
        return ResponseEntity.ok(ApiResponse.success(moduleService.getAllModules()));
    }

    @PostMapping
    @Operation(summary = "Create a new module")
    public ResponseEntity<ApiResponse<ModuleResponse>> createModule(@Valid @RequestBody ModuleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(moduleService.createModule(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a module")
    public ResponseEntity<ApiResponse<ModuleResponse>> updateModule(@PathVariable Integer id, @Valid @RequestBody ModuleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(moduleService.updateModule(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a module")
    public ResponseEntity<ApiResponse<Void>> deleteModule(@PathVariable Integer id) {
        moduleService.deleteModule(id);
        return ResponseEntity.ok(ApiResponse.success("Module deleted successfully", null));
    }
}
