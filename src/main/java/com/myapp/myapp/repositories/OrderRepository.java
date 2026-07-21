package com.myapp.myapp.repositories;

import com.myapp.myapp.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Ödəniş callback-i qayıdanda sifarişi transactionId üzərindən tapmaq üçün
    Optional<Order> findByTransactionId(String transactionId);
}