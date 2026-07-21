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
import com.myapp.myapp.services.EmailService;
import com.myapp.myapp.services.OrderService;

import jakarta.persistence.EntityNotFoundException;

import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final EmailService emailService;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            ModelMapper modelMapper,
            EmailService emailService
    ) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.emailService = emailService;
    }

    /**
     * Order obyektini OrderDto-ya çevirir.
     *
     * Order.status enum tipindədir,
     * OrderDto.status isə String tipindədir.
     * Buna görə statusun Azərbaycanca label dəyərini ayrıca təyin edirik.
     */
    private OrderDto mapToDto(Order order) {
        OrderDto dto = modelMapper.map(order, OrderDto.class);

        if (order.getStatus() != null) {
            dto.setStatus(order.getStatus().getLabel());
        }

        return dto;
    }

    /**
     * String status dəyərini OrderStatus enum-una çevirir.
     *
     * Həm Azərbaycanca label qəbul edir:
     * "Hazırlanır"
     *
     * Həm də enum adı qəbul edir:
     * "PENDING"
     */
    private OrderStatus parseOrderStatus(String status) {

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException(
                    "Sifariş statusu boş ola bilməz."
            );
        }

        OrderStatus orderStatus = OrderStatus.fromLabel(status);

        if (orderStatus != null) {
            return orderStatus;
        }

        try {
            return OrderStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Naməlum sifariş statusu: " + status
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Sifariş tapılmadı, ID: " + id
                        )
                );

        return mapToDto(order);
    }

    @Override
    @Transactional
    public OrderDto createOrder(OrderCreateDto orderCreateDto) {

        Order order = new Order();

        order.setCustomerName(orderCreateDto.getCustomerName());
        order.setCustomerEmail(orderCreateDto.getCustomerEmail());
        order.setCustomerPhone(orderCreateDto.getCustomerPhone());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(orderCreateDto.getPaymentMethod());

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemDto itemDto : orderCreateDto.getOrderItems()) {

            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() ->
                            new EntityNotFoundException(
                                    "Məhsul tapılmadı, ID: "
                                            + itemDto.getProductId()
                            )
                    );

            OrderItem orderItem = new OrderItem();

            orderItem.setProduct(product);

            // Sifariş zamanı məhsulun adını snapshot kimi saxlayırıq
            orderItem.setProductName(product.getName());

            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setPrice(product.getPrice());

            // Order ilə OrderItem arasındakı əlaqəni düzgün qurur
            order.addOrderItem(orderItem);

            BigDecimal itemTotal = product.getPrice()
                    .multiply(
                            BigDecimal.valueOf(itemDto.getQuantity())
                    );

            totalAmount = totalAmount.add(itemTotal);
        }

        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        sendOrderConfirmationEmail(savedOrder);

        return mapToDto(savedOrder);
    }

    /**
     * Sifariş təsdiqi ilə bağlı e-poçt göndərir.
     *
     * E-poçt göndərilməsə belə,
     * sifarişin yaradılması ləğv edilmir.
     */
    private void sendOrderConfirmationEmail(Order order) {

        try {
            String subject = "Sifariş təsdiqi! #" + order.getId();

            String body = String.format(
                    "Hörmətli %s,<br><br>"
                            + "Sifarişiniz (%s AZN) uğurla qəbul edildi. "
                            + "Tezliklə sizinlə əlaqə saxlayacağıq.<br><br>"
                            + "Sifariş ID: %d",

                    order.getCustomerName(),
                    order.getTotalAmount(),
                    order.getId()
            );

            emailService.sendOrderConfirmationEmail(
                    order.getCustomerEmail(),
                    subject,
                    body
            );

        } catch (Exception e) {

            log.error(
                    "Sifariş təsdiqi e-poçtu göndərilərkən xəta baş verdi. "
                            + "Sifariş ID: {}",
                    order.getId(),
                    e
            );
        }
    }

    @Override
    @Transactional
    public OrderDto updateOrderStatus(Long id, String status) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Sifariş tapılmadı, ID: " + id
                        )
                );

        OrderStatus orderStatus = parseOrderStatus(status);

        order.setStatus(orderStatus);

        Order updatedOrder = orderRepository.save(order);

        return mapToDto(updatedOrder);
    }

    @Override
    public OrderDto attachTransactionId(Long orderId, String transactionId) {
        return null;
    }

    @Override
    public OrderDto confirmPaymentByTransactionId(String transactionId, boolean success) {
        return null;
    }

    @Override
    public Long countOrders() {
        return null;
    }

}