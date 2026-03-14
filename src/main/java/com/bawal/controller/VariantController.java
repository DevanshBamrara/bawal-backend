package com.bawal.controller;

import com.bawal.dto.request.VariantRequest;
import com.bawal.dto.response.ApiResponse;
import com.bawal.dto.response.VariantResponse;
import com.bawal.service.VariantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Variants", description = "Product variant (size) management APIs")
public class VariantController {

    private final VariantService variantService;

    @PostMapping("/variants")
    @Operation(summary = "Create a new variant for a product")
    public ResponseEntity<ApiResponse<VariantResponse>> create(@Valid @RequestBody VariantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Variant created", variantService.createVariant(request)));
    }

    @GetMapping("/products/{productId}/variants")
    @Operation(summary = "Get all variants for a product")
    public ResponseEntity<ApiResponse<List<VariantResponse>>> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(variantService.getVariantsByProduct(productId)));
    }
}
