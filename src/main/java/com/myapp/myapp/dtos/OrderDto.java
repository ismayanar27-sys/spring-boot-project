package com.myapp.myapp.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {

    private Long id;

    private String customerName;

    private String customerEmail;

    private String customerPhone;

    // Pul üçün BigDecimal istifadə olunur
    private BigDecimal totalAmount;

    // OrderStatus enum-u frontend üçün String kimi qaytarılır
    private String status;

    private LocalDateTime orderDate;

    private String paymentMethod;

    private List<OrderItemDto> orderItems;
}