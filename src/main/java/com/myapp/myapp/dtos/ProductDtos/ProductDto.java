package com.myapp.myapp.dtos.ProductDtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String photoUrl;

    // Yeni əlavə olunan sahələr
    private String brand;
    private Integer volume;
    private String scent;
    private String category;
}