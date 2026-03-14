package com.bawal.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LowStockAlertResponse {
    private Long variantId;
    private String productName;
    private String size;
    private String sku;
    private int currentStock;
}
