package com.myapp.myapp.dtos.ProductDtos;

import lombok.Data;

@Data
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String photoUrl;

    // Yeni əlavə olunan sahələr
    private String brand;
    private Integer volume;
    private String scent;
    private String category;
}