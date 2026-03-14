package com.bawal.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean active;
    private LocalDateTime createdAt;
    private List<VariantResponse> variants;
    private List<ImageResponse> images;
    private int totalStock;
}
