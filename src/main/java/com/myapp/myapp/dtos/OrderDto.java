package com.myapp.myapp.dtos;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
    private Long id;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private Double totalAmount;
    private String status;
    private LocalDateTime orderDate;
    private List<OrderItemDto> orderItems;
}