package com.myapp.myapp.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Sifariş zamanı seçilən məhsul
// LAZY loading performans üçün istifadə olunur
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Sifariş zamanı məhsulun adı
// Məhsul sonradan dəyişsə belə, köhnə sifarişdəki ad qorunur
    @Column(nullable = false)
    private String productName;

    // Sifariş edilən məhsul sayı
    @Column(nullable = false)
    private Integer quantity;

    // Sifariş zamanı məhsulun qiyməti
// Product qiyməti sonradan dəyişsə belə, köhnə qiymət qorunur
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    // Bu OrderItem-in aid olduğu sifariş
// LAZY loading performans üçün istifadə olunur
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

}