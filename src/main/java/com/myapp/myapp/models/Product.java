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
        private String name;

        private String description;
        private Double price;
        private String photoUrl;

        private String brand;
        private Integer volume;
        private String scent;

        private String category;
}