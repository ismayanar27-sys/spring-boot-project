package com.myapp.myapp.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="products")
@Data
public class Product {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(unique = true)
        private String name; // Ətrin adı

        private String description; // Ətrin təsviri
        private Double price; // Qiyməti
        private String photoUrl; // Şəkil URL-i

        // Yeni əlavə olunan sahələr
        private String brand; // Ətrin brendi (məsələn, Dior, Chanel)
        private Integer volume; // Həcmi (məsələn, 50, 100 ml)
        private String scent; // Qoxusu/Tərkibi (məsələn, çiçəkli, şirin)

        // Əlavə olunan Kateqoriya sahəsi
        private String category;
}