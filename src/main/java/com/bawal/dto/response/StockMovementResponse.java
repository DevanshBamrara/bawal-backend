package com.bawal.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovementResponse {
    private Long id;
    private String productName;
    private String size;
    private String type;
    private int quantity;
    private String reason;
    private LocalDateTime createdAt;
}
