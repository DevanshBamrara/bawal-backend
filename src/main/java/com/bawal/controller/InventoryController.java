package com.bawal.controller;

import com.bawal.dto.request.StockRequest;
import com.bawal.dto.response.*;
import com.bawal.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Stock management APIs")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/stock-in")
    @Operation(summary = "Add stock for a variant")
    public ResponseEntity<ApiResponse<InventoryResponse>> stockIn(@Valid @RequestBody StockRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Stock added", inventoryService.stockIn(request)));
    }

    @PostMapping("/stock-out")
    @Operation(summary = "Remove stock for a variant")
    public ResponseEntity<ApiResponse<InventoryResponse>> stockOut(@Valid @RequestBody StockRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Stock removed", inventoryService.stockOut(request)));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock alerts")
    public ResponseEntity<ApiResponse<List<LowStockAlertResponse>>> getLowStock() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getLowStockAlerts()));
    }

    @GetMapping("/movements")
    @Operation(summary = "Get all stock movements")
    public ResponseEntity<ApiResponse<List<StockMovementResponse>>> getAllMovements() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getAllMovements()));
    }

    @GetMapping("/movements/{variantId}")
    @Operation(summary = "Get stock movements for a variant")
    public ResponseEntity<ApiResponse<List<StockMovementResponse>>> getMovementsByVariant(
            @PathVariable Long variantId) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getMovementsByVariant(variantId)));
    }
}
