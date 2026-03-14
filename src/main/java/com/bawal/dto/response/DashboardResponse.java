package com.bawal.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {
    private long totalProducts;
    private long totalOrders;
    private long lowStockCount;
    private BigDecimal totalRevenue;
    private List<OrderResponse> recentOrders;
    private List<LowStockAlertResponse> lowStockAlerts;
}
