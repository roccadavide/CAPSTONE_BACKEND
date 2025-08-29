package daviderocca.CAPSTONE_BACKEND.DTO;

import daviderocca.CAPSTONE_BACKEND.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponseDTO(
        UUID orderId,
        String customerName,
        String customerEmail,
        String customerPhone,
        OrderStatus orderStatus,
        LocalDateTime createdAt,
        UUID userId,
        List<OrderItemResponseDTO> orderItems
) {}