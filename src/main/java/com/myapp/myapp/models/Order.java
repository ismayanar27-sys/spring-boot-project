package com.myapp.myapp.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Müştərinin sifariş zamanı daxil etdiyi adı
    @Column(nullable = false)
    private String customerName;

    // Müştərinin sifariş zamanı daxil etdiyi e-poçt ünvanı
    @Column(nullable = false)
    private String customerEmail;

    // Müştərinin sifariş zamanı daxil etdiyi telefon nömrəsi
    @Column(nullable = false)
    private String customerPhone;

    // Sifarişin ümumi məbləği
// Pul üçün BigDecimal istifadə olunur
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    // Sifarişin cari statusu
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    // Sifarişin yaradılma vaxtı
    @Column(nullable = false)
    private LocalDateTime orderDate;

    // Sifariş zamanı seçilmiş ödəniş üsulu
    @Column(nullable = false)
    private String paymentMethod;

    // Ödəniş provayderinə (Portmanat) göndərilən unikal əməliyyat ID-si.
    // Callback qayıdanda məhz bu ID vasitəsilə hansı sifarişə aid olduğunu tapırıq.
    // nullable=true, çünki sifariş yaradılan anda hələ ödənişə keçilməmiş ola bilər.
    @Column(unique = true)
    private String transactionId;

    // Sifarişə daxil olan məhsullar
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OrderItem> orderItems = new ArrayList<>();

    // Sifariş bazaya ilk dəfə yazılmadan əvvəl avtomatik işləyir
    @PrePersist
    protected void onCreate() {
        this.orderDate = LocalDateTime.now();
    }

    // Sifarişə məhsul əlavə edir
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    // Sifarişdən məhsul silir
    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
    }
}