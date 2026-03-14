package com.bawal.service;

import com.bawal.dto.request.ImageRequest;
import com.bawal.dto.response.ImageResponse;
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
public class ImageService {

    private final ProductImageRepository imageRepository;
    private final ProductRepository productRepository;

    @Transactional
    public ImageResponse addImage(ImageRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        ProductImage image = ProductImage.builder()
                .product(product)
                .imageUrl(request.getImageUrl())
                .imageType(request.getImageType())
                .build();
        image = imageRepository.save(image);
        return mapToResponse(image);
    }

    public List<ImageResponse> getImagesByProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
        return imageRepository.findByProductId(productId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public void deleteImage(Long id) {
        if (!imageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Image not found with id: " + id);
        }
        imageRepository.deleteById(id);
    }

    private ImageResponse mapToResponse(ProductImage image) {
        return ImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .imageType(image.getImageType() != null ? image.getImageType().name() : null)
                .build();
    }
}
