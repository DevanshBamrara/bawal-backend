package com.bawal.dto.request;

import com.bawal.model.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusRequest {

    @NotNull(message = "Status is required")
    private OrderStatus status;
}
