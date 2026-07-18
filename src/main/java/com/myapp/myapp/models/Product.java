package com.myapp.myapp.models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name="products")
@Data
public class Product {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(unique = true)
        private String name;

        private String description;
        private BigDecimal price;
        private String photoUrl;

        private String brand;
        private Integer volume;
        private String scent;

        private String category;
}