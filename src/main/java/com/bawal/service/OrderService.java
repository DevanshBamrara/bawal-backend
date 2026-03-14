package com.bawal.service;

import com.bawal.dto.request.*;
import com.bawal.dto.response.*;
import com.bawal.exception.*;
import com.bawal.model.*;
import com.bawal.model.enums.MovementType;
import com.bawal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductVariantRepository variantRepository;
    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        Order order = Order.builder()
                .customerName(request.getCustomerName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .items(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.getItems()) {
            ProductVariant variant = variantRepository.findById(itemReq.getVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Variant not found: " + itemReq.getVariantId()));

            Inventory inventory = inventoryRepository.findByVariantId(variant.getId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Inventory not found for variant: " + variant.getId()));

            if (inventory.getQuantity() < itemReq.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for " + variant.getProduct().getName()
                        + " (" + variant.getSize() + "). Available: " + inventory.getQuantity()
                        + ", Requested: " + itemReq.getQuantity());
            }

            BigDecimal itemPrice = variant.getProduct().getPrice();
            totalAmount = totalAmount.add(itemPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity())));

            OrderItem orderItem = OrderItem.builder()
                    .order(order).variant(variant).quantity(itemReq.getQuantity()).price(itemPrice).build();
            order.getItems().add(orderItem);

            // Deduct inventory
            inventory.setQuantity(inventory.getQuantity() - itemReq.getQuantity());
            inventory.setUpdatedAt(LocalDateTime.now());
            inventoryRepository.save(inventory);

            // Record stock movement
            stockMovementRepository.save(StockMovement.builder()
                    .variant(variant).type(MovementType.OUT).quantity(itemReq.getQuantity())
                    .reason("Order placed").build());
        }

        order.setTotalAmount(totalAmount);
        order = orderRepository.save(order);
        return mapToResponse(order);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return mapToResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatusRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        order.setStatus(request.getStatus());
        order = orderRepository.save(order);
        return mapToResponse(order);
    }

    public OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productName(item.getVariant().getProduct().getName())
                        .size(item.getVariant().getSize().name())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId()).customerName(order.getCustomerName()).email(order.getEmail())
                .phone(order.getPhone()).address(order.getAddress()).totalAmount(order.getTotalAmount())
                .status(order.getStatus().name()).createdAt(order.getCreatedAt()).items(items).build();
    }
}
