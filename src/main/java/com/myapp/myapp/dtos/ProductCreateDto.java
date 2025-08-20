package com.myapp.myapp.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProductCreateDto {
    private String name;
    private String description;
    private Double price;

    // Yeni əlavə olunan sahələr
    private String brand;
    private Integer volume;
    private String scent;
    private String category;
}