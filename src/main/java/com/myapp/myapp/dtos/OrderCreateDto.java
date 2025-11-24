package com.myapp.myapp.dtos;

import lombok.Data;
import java.util.List;

@Data
public class OrderCreateDto {
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private List<OrderItemDto> orderItems;
    private String paymentMethod;
}