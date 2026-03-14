package com.bawal.service;

import com.bawal.dto.response.*;
import com.bawal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderService orderService;

    @Value("${bawal.inventory.low-stock-threshold:5}")
    private int lowStockThreshold;

    public DashboardResponse getDashboard() {
        long totalProducts = productRepository.findByIsActiveTrue().size();
        long totalOrders = orderRepository.count();

        List<LowStockAlertResponse> lowStockAlerts = inventoryRepository.findLowStock(lowStockThreshold).stream()
                .map(inv -> LowStockAlertResponse.builder()
                        .variantId(inv.getVariant().getId())
                        .productName(inv.getVariant().getProduct().getName())
                        .size(inv.getVariant().getSize().name())
                        .sku(inv.getVariant().getSku())
                        .currentStock(inv.getQuantity())
                        .build())
                .collect(Collectors.toList());

        List<OrderResponse> recentOrders = orderRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(orderService::mapToResponse).collect(Collectors.toList());

        BigDecimal totalRevenue = orderRepository.findAll().stream()
                .map(o -> o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DashboardResponse.builder()
                .totalProducts(totalProducts).totalOrders(totalOrders).lowStockCount(lowStockAlerts.size())
                .totalRevenue(totalRevenue).recentOrders(recentOrders).lowStockAlerts(lowStockAlerts).build();
    }
}
