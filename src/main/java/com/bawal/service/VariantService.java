package com.bawal.service;

import com.bawal.dto.request.VariantRequest;
import com.bawal.dto.response.VariantResponse;
import com.bawal.exception.ResourceNotFoundException;
import com.bawal.model.*;
import com.bawal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VariantService {

    private final ProductVariantRepository variantRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    public VariantResponse createVariant(VariantRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        String sku = generateSku(product.getName(), request.getSize().name());

        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .size(request.getSize())
                .sku(sku)
                .build();
        variant = variantRepository.save(variant);

        // Auto-create inventory with 0 stock
        Inventory inventory = Inventory.builder().variant(variant).quantity(0).build();
        inventoryRepository.save(inventory);
        variant.setInventory(inventory);

        return mapToResponse(variant);
    }

    public List<VariantResponse> getVariantsByProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
        return variantRepository.findByProductId(productId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    private String generateSku(String productName, String size) {
        String slug = productName.toUpperCase()
                .replaceAll("[^A-Z0-9]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
        if (slug.length() > 20)
            slug = slug.substring(0, 20);
        return slug + "-" + size;
    }

    private VariantResponse mapToResponse(ProductVariant variant) {
        return VariantResponse.builder()
                .id(variant.getId())
                .size(variant.getSize().name())
                .sku(variant.getSku())
                .stock(variant.getInventory() != null ? variant.getInventory().getQuantity() : 0)
                .build();
    }
}
