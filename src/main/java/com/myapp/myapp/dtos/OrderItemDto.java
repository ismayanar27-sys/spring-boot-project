package com.myapp.myapp.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDto {

    @NotNull(message = "Məhsul seçilməlidir.")
    private Long productId;

    private String productName;

    @NotNull(message = "Məhsulun sayı daxil edilməlidir.")
    @Min(value = 1, message = "Məhsulun sayı ən azı 1 olmalıdır.")
    private Integer quantity;

    private BigDecimal price;

}