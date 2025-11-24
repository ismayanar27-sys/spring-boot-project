package com.myapp.myapp.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "order_items")
@Data
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product; // Sifariş edilən məhsul

    @Column(nullable = false)
    private Integer quantity; // Miqdarı

    @Column(nullable = false)
    private Double price; // Sifariş olunan zaman məhsulun qiyməti

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order; // Bu maddənin aid olduğu sifariş
}