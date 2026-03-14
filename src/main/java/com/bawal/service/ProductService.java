package com.bawal.service;

import com.bawal.dto.request.ProductRequest;
import com.bawal.dto.response.*;
import com.bawal.exception.ResourceNotFoundException;
import com.bawal.model.Product;
import com.bawal.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .build();
        product = productRepository.save(product);
        return mapToResponse(product);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findByIsActiveTrue().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product = productRepository.save(product);
        return mapToResponse(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setIsActive(false);
        productRepository.save(product);
    }

    public ProductResponse mapToResponse(Product product) {
        List<VariantResponse> variants = product.getVariants().stream()
                .map(v -> VariantResponse.builder()
                        .id(v.getId())
                        .size(v.getSize().name())
                        .sku(v.getSku())
                        .stock(v.getInventory() != null ? v.getInventory().getQuantity() : 0)
                        .build())
                .collect(Collectors.toList());

        List<ImageResponse> images = product.getImages().stream()
                .map(i -> ImageResponse.builder()
                        .id(i.getId())
                        .imageUrl(i.getImageUrl())
                        .imageType(i.getImageType() != null ? i.getImageType().name() : null)
                        .build())
                .collect(Collectors.toList());

        int totalStock = variants.stream().mapToInt(VariantResponse::getStock).sum();

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .active(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .variants(variants)
                .images(images)
                .totalStock(totalStock)
                .build();
    }
}
