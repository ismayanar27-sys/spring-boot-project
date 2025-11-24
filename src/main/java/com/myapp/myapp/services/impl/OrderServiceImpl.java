package com.myapp.myapp.services.impl;

import com.myapp.myapp.dtos.OrderCreateDto;
import com.myapp.myapp.dtos.OrderDto;
import com.myapp.myapp.dtos.OrderItemDto;
import com.myapp.myapp.models.Order;
import com.myapp.myapp.models.OrderItem;
import com.myapp.myapp.models.Product;
import com.myapp.myapp.repositories.OrderRepository;
import com.myapp.myapp.repositories.ProductRepository;
import com.myapp.myapp.services.EmailService; // Əlavə olundu
import com.myapp.myapp.services.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final EmailService emailService; // Əlavə olundu

    // Konstruktor yeniləndi: EmailService daxil edildi
    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository, ModelMapper modelMapper, EmailService emailService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.emailService = emailService; // Başlanğıc dəyəri verilir
    }

    @Override
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> modelMapper.map(order, OrderDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto getOrderById(Long id) {
        // orElse(null) əvəzinə, tapa bilmədikdə daha mənalı bir xəta ata bilərsiniz
        return orderRepository.findById(id)
                .map(order -> modelMapper.map(order, OrderDto.class))
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
        order.setStatus("Hazırlanır");
        order.setPaymentMethod(orderCreateDto.getPaymentMethod());

        Double totalAmount = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemDto itemDto : orderCreateDto.getOrderItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Məhsul tapılmadı, ID: " + itemDto.getProductId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setOrder(order);

            totalAmount += orderItem.getPrice() * orderItem.getQuantity();
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
            System.err.println("Sifariş təsdiqi e-poçtu göndərilməsi zamanı xəta baş verdi: " + e.getMessage());
        }

        return modelMapper.map(savedOrder, OrderDto.class);
    }

    @Override
    @Transactional
    public OrderDto updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sifariş tapılmadı, ID: " + id));

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);

        return modelMapper.map(updatedOrder, OrderDto.class);
    }
}