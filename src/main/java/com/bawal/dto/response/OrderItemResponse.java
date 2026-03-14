package com.bawal.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {
    private Long id;
    private String productName;
    private String size;
    private int quantity;
    private BigDecimal price;
}
