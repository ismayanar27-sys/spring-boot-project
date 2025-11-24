package com.myapp.myapp.services;

import com.myapp.myapp.dtos.OrderCreateDto;
import com.myapp.myapp.dtos.OrderDto;
import java.util.List;

public interface OrderService {
    // Bütün sifarişləri gətirir
    List<OrderDto> getAllOrders();

    // ID-yə görə sifarişi tapır
    OrderDto getOrderById(Long id);

    // Yeni sifariş yaradır
    OrderDto createOrder(OrderCreateDto orderCreateDto);

    // Sifarişin statusunu yeniləyir
    OrderDto updateOrderStatus(Long id, String status);
}