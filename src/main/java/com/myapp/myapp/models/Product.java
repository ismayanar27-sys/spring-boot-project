package com.myapp.myapp.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {

        // JPA Entity-lərdə @Data istifadə etmirik.
        // equals(), hashCode() və toString() metodlarının avtomatik yaradılması
        // Entity identity və relationship-lərlə bağlı problemlər yarada bilər.
        // Buna görə yalnız @Getter və @Setter istifadə olunur.

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true)
        private String name;

        @Column(nullable = false, length = 1000)
        private String description;

        @Column(nullable = false, precision = 10, scale = 2)
        private BigDecimal price;

        @Column(nullable = false)
        private String photoUrl;

        @Column(nullable = false)
        private Integer preparationTime;

        @Column(nullable = false)
        private String category;
}