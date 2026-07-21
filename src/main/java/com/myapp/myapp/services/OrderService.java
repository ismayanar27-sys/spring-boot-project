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

    // Ödənişə keçərkən (createPayment metodunda) sifarişə unikal transactionId
    // "bağlayır" ki, ödəniş provayderindən callback qayıdanda məhz bu ID
    // vasitəsilə hansı sifarişə aid olduğunu tapa bilək.
    OrderDto attachTransactionId(Long orderId, String transactionId);

    // Ödəniş provayderindən (Portmanat) gələn callback-i emal edir:
    // transactionId-yə görə sifarişi tapır və nəticəyə uyğun statusunu
    // PAID və ya FAILED edir
    //  "void" yox, "OrderDto" qaytarır - hazırda PaymentController bu
    // qaytarılan dəyəri istifadə etmir, amma gələcəkdə (məsələn admin
    // panelində "son ödənilən sifariş" göstərmək üçün) bu, əlavə bir
    // getOrderById() sorğusu yazmadan əlimizdə olacaq.
    OrderDto confirmPaymentByTransactionId(String transactionId, boolean success);

    Long countOrders();
}