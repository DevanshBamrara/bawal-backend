package com.bawal.service;

import com.bawal.dto.request.StockRequest;
import com.bawal.dto.response.*;
import com.bawal.exception.*;
import com.bawal.model.*;
import com.bawal.model.enums.MovementType;
import com.bawal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductVariantRepository variantRepository;
    private final StockMovementRepository stockMovementRepository;

    @Value("${bawal.inventory.low-stock-threshold:5}")
    private int lowStockThreshold;

    @Transactional
    public InventoryResponse stockIn(StockRequest request) {
        ProductVariant variant = variantRepository.findById(request.getVariantId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Variant not found with id: " + request.getVariantId()));
        Inventory inventory = inventoryRepository.findByVariantId(request.getVariantId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for variant: " + request.getVariantId()));

        inventory.setQuantity(inventory.getQuantity() + request.getQuantity());
        inventory.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inventory);

        stockMovementRepository.save(StockMovement.builder()
                .variant(variant).type(MovementType.IN).quantity(request.getQuantity())
                .reason(request.getReason() != null ? request.getReason() : "Stock in").build());

        return mapToResponse(inventory);
    }

    @Transactional
    public InventoryResponse stockOut(StockRequest request) {
        ProductVariant variant = variantRepository.findById(request.getVariantId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Variant not found with id: " + request.getVariantId()));
        Inventory inventory = inventoryRepository.findByVariantId(request.getVariantId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for variant: " + request.getVariantId()));

        if (inventory.getQuantity() < request.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for " + variant.getSku() +
                    ". Available: " + inventory.getQuantity() + ", Requested: " + request.getQuantity());
        }

        inventory.setQuantity(inventory.getQuantity() - request.getQuantity());
        inventory.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inventory);

        stockMovementRepository.save(StockMovement.builder()
                .variant(variant).type(MovementType.OUT).quantity(request.getQuantity())
                .reason(request.getReason() != null ? request.getReason() : "Stock out").build());

        return mapToResponse(inventory);
    }

    public List<LowStockAlertResponse> getLowStockAlerts() {
        return inventoryRepository.findLowStock(lowStockThreshold).stream()
                .map(inv -> LowStockAlertResponse.builder()
                        .variantId(inv.getVariant().getId())
                        .productName(inv.getVariant().getProduct().getName())
                        .size(inv.getVariant().getSize().name())
                        .sku(inv.getVariant().getSku())
                        .currentStock(inv.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }

    public List<StockMovementResponse> getAllMovements() {
        return stockMovementRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapMovementToResponse).collect(Collectors.toList());
    }

    public List<StockMovementResponse> getMovementsByVariant(Long variantId) {
        return stockMovementRepository.findByVariantIdOrderByCreatedAtDesc(variantId).stream()
                .map(this::mapMovementToResponse).collect(Collectors.toList());
    }

    private InventoryResponse mapToResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .variantId(inventory.getVariant().getId())
                .productName(inventory.getVariant().getProduct().getName())
                .size(inventory.getVariant().getSize().name())
                .sku(inventory.getVariant().getSku())
                .quantity(inventory.getQuantity())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }

    private StockMovementResponse mapMovementToResponse(StockMovement movement) {
        return StockMovementResponse.builder()
                .id(movement.getId())
                .productName(movement.getVariant().getProduct().getName())
                .size(movement.getVariant().getSize().name())
                .type(movement.getType().name())
                .quantity(movement.getQuantity())
                .reason(movement.getReason())
                .createdAt(movement.getCreatedAt())
                .build();
    }
}
