package com.myapp.myapp.models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ordered product  - LAZY loading for performance
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // Quantity
    @Column(nullable = false)
    private Integer quantity;

    // Price at the time of order
    @Column(nullable = false)
    private BigDecimal price;

    // Parent order  - LAZY loading
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}