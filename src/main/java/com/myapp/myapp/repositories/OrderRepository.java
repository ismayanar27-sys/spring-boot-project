package com.myapp.myapp.repositories;

import com.myapp.myapp.models.Order;
import com.myapp.myapp.models.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Ödəniş callback-i qayıdanda sifarişi transactionId üzərindən tapmaq üçün
    Optional<Order> findByTransactionId(String transactionId);

    //(Statistics üçün): statusa görə sifariş sayını birbaşa bazada hesablayır.
    long countByStatus(OrderStatus status);

    // (Statistics üçün): yalnız PAID (ödənilmiş) sifarişlərin
    // ümumi məbləğini bazada cəmləyir. Heç bir PAID sifariş yoxdursa,
    // SUM() SQL-də NULL qaytarır - COALESCE ilə bunun əvəzinə 0 qaytarılır.
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = :status")
    BigDecimal sumTotalAmountByStatus(@Param("status") OrderStatus status);
}