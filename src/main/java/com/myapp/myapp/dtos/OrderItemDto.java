package com.myapp.myapp.dtos;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double price;
}