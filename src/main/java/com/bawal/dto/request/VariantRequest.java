package com.bawal.dto.request;

import com.bawal.model.enums.Size;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariantRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Size is required")
    private Size size;
}
