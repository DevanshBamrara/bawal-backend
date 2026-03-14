package com.bawal.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantResponse {
    private Long id;
    private String size;
    private String sku;
    private int stock;
}
