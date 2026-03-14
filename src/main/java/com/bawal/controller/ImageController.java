package com.bawal.controller;

import com.bawal.dto.request.ImageRequest;
import com.bawal.dto.response.ApiResponse;
import com.bawal.dto.response.ImageResponse;
import com.bawal.service.ImageService;
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
@Tag(name = "Images", description = "Product image management APIs")
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/images")
    @Operation(summary = "Add image to a product")
    public ResponseEntity<ApiResponse<ImageResponse>> add(@Valid @RequestBody ImageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Image added", imageService.addImage(request)));
    }

    @GetMapping("/products/{productId}/images")
    @Operation(summary = "Get all images for a product")
    public ResponseEntity<ApiResponse<List<ImageResponse>>> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(imageService.getImagesByProduct(productId)));
    }

    @DeleteMapping("/images/{id}")
    @Operation(summary = "Delete an image")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        imageService.deleteImage(id);
        return ResponseEntity.ok(ApiResponse.success("Image deleted", null));
    }
}
