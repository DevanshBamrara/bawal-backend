package com.bawal.dto.request;

import com.bawal.model.enums.ImageType;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    @NotNull(message = "Image type is required")
    private ImageType imageType;
}
