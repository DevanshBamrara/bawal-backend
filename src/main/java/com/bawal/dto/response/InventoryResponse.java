package com.bawal.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponse {
    private Long variantId;
    private String productName;
    private String size;
    private String sku;
    private int quantity;
    private LocalDateTime updatedAt;
}
