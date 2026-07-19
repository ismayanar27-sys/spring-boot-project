package com.myapp.myapp.services.impl;

import com.myapp.myapp.dtos.OrderCreateDto;
import com.myapp.myapp.dtos.OrderDto;
import com.myapp.myapp.dtos.OrderItemDto;
import com.myapp.myapp.models.Order;
import com.myapp.myapp.models.OrderItem;
import com.myapp.myapp.models.OrderStatus;
import com.myapp.myapp.models.Product;
import com.myapp.myapp.repositories.OrderRepository;
import com.myapp.myapp.repositories.ProductRepository;
import com.myapp.myapp.services.EmailService; // Əlavə olundu
import com.myapp.myapp.services.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final EmailService emailService;

    // Konstruktor yeniləndi: EmailService daxil edildi
    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository, ModelMapper modelMapper, EmailService emailService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.emailService = emailService; // Başlanğıc dəyəri verilir
    }

    /**
     * ELAVE EDILDI: köməkçi metod - Order.status artıq OrderStatus (enum) tipindədir,
     * amma OrderDto.status hələ də String-dir (frontend-ə "Hazırlanır" kimi Azərbaycanca
     * mətn göstərmək üçün). ModelMapper enum-u avtomatik "label"-ə çevirə bilmir,
     * ona görə bunu əl ilə edirik.
     */
    private OrderDto mapToDto(Order order) {
        OrderDto dto = modelMapper.map(order, OrderDto.class);
        if (order.getStatus() != null) {
            dto.setStatus(order.getStatus().getLabel());
        }
        return dto;
    }

    @Override
    @Transactional(readOnly = true) //LazyInitializationException-in qarşısını alır
    // (order.getOrderItems() lazy-yüklənir, ModelMapper ona toxunanda tranzaksiya
    // artıq bağlı olmasa xəta verər - "readOnly = true" bunu təhlükəsiz açıq saxlayır)
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToDto) // DƏYİŞDİ: modelMapper.map(...) -> mapToDto (enum->label çevrilməsi üçün)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true) // ELAVE EDILDI: eyni səbəbdən (LazyInitialization qorunması)
    public OrderDto getOrderById(Long id) {
        // orElse(null) əvəzinə, tapa bilmədikdə daha mənalı bir xəta ata bilərsiniz
        return orderRepository.findById(id)
                .map(this::mapToDto) // DƏYİŞDİ: modelMapper.map(...) -> mapToDto
                .orElse(null);
    }

    @Override
    @Transactional
    public OrderDto createOrder(OrderCreateDto orderCreateDto) {
        Order order = new Order();
        order.setCustomerName(orderCreateDto.getCustomerName());
        order.setCustomerEmail(orderCreateDto.getCustomerEmail());
        order.setCustomerPhone(orderCreateDto.getCustomerPhone());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING); // DÜZƏLDİLDİ: .getLabel() silindi - Order.status artıq birbaşa OrderStatus (enum)
        order.setPaymentMethod(orderCreateDto.getPaymentMethod());

        BigDecimal totalAmount = BigDecimal.ZERO; // DƏYİŞDİ: Double 0.0 -> BigDecimal.ZERO
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemDto itemDto : orderCreateDto.getOrderItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Məhsul tapılmadı, ID: " + itemDto.getProductId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setOrder(order);

            // DƏYİŞDİ: Double vurma/toplama əvəzinə BigDecimal-ın öz metodları (dəqiq hesablama üçün)
            BigDecimal itemTotal = orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        // YENİ ƏLAVƏ: Sifariş təsdiq e-poçtunu göndərmək
        try {
            String subject = "Sifariş Təsdiqi! #" + savedOrder.getId();
            String body = String.format(
                    "Hörmətli %s,\n\nSifarişiniz (%s AZN) uğurla qəbul edildi. Tezliklə sizinlə əlaqə saxlayacağıq.\n\nSifariş ID: %d",
                    savedOrder.getCustomerName(), savedOrder.getTotalAmount(), savedOrder.getId()
            );

            emailService.sendOrderConfirmationEmail(savedOrder.getCustomerEmail(), subject, body);
        } catch (Exception e) {
            // E-poçt göndərilməsə belə, sifarişin yaradılmasını ləğv etmirik
            log.error("Sifariş təsdiqi e-poçtu göndərilməsi zamanı xəta baş verdi", e); // DƏYİŞDİ: System.err -> log.error
        }

        return mapToDto(savedOrder); // DƏYİŞDİ: modelMapper.map(...) -> mapToDto
    }

    @Override
    @Transactional
    public OrderDto updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sifariş tapılmadı, ID: " + id));

        // Status artıq sərbəst mətn kimi qəbul edilmir. Admin panelinin
        // formu Azərbaycanca mətn ("Hazırlanır") göndərdiyi üçün, əvvəlcə label ilə
        // axtarırıq; tapmasa, enum adı ("PENDING") kimi də yoxlayırıq (PaymentController
        // "PAID"/"FAILED" göndərəndə bu yol işə düşür). Heç biri uyğun gəlməsə, xəta atılır.
        OrderStatus orderStatus = OrderStatus.fromLabel(status);
        if (orderStatus == null) {
            try {
                orderStatus = OrderStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Naməlum sifariş statusu: " + status +
                        ". Mümkün dəyərlər: Hazırlanır, Ödənildi, Uğursuz, Göndərildi, Ləğv edildi");
            }
        }

        order.setStatus(orderStatus); // DÜZƏLDİLDİ: .getLabel() silindi - birbaşa enum-un özü set edilir
        Order updatedOrder = orderRepository.save(order);

        return mapToDto(updatedOrder); // DƏYİŞDİ: modelMapper.map(...) -> mapToDto
    }
}