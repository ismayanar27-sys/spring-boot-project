package com.myapp.myapp.controllers;

import com.myapp.myapp.dtos.OrderCreateDto;
import com.myapp.myapp.dtos.OrderDto;
import com.myapp.myapp.services.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private static final String CART_SESSION_KEY = "CURRENT_ORDER_ID";

    private final OrderService orderService;

    // application.properties-dən oxunur: portmanat.merchant-id=${PORTMANAT_MERCHANT_ID}
    @Value("${portmanat.merchant-id:}")
    private String merchantId;

    @PostMapping
    @ResponseBody
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody OrderCreateDto orderCreateDto, HttpSession session) {
        OrderDto createdOrder = orderService.createOrder(orderCreateDto);
        // Yeni sifariş yaradılanda, ID-ni bu istifadəçinin sessiyasında saxlayır
        session.setAttribute(CART_SESSION_KEY, createdOrder.getId());
        return ResponseEntity.ok(createdOrder);
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<OrderDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        try {
            OrderDto order = orderService.getOrderById(id);
            return ResponseEntity.ok(order);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        Long currentOrderId = (Long) session.getAttribute(CART_SESSION_KEY);
        OrderDto order = null;

        if (currentOrderId != null) {
            try {
                order = orderService.getOrderById(currentOrderId);
            } catch (EntityNotFoundException e) {
                // Sifariş artıq mövcud deyil (silinib və s.) boş səbət göstərir
                log.warn("Sessiyadakı sifariş tapılmadı, sessiya təmizlənir. orderId={}", currentOrderId);
                session.removeAttribute(CART_SESSION_KEY);
            }
        }

        if (order != null) {
            model.addAttribute("order", order);
        } else {
            model.addAttribute("message", "Səbətinizdə hələ heç bir sifariş yoxdur.");
        }
        return "admin/checkout/cart";
    }

    @PostMapping("/create-payment")
    public String createPayment(@RequestParam("orderId") Long orderId) {

        OrderDto order;
        try {
            order = orderService.getOrderById(orderId);
        } catch (EntityNotFoundException e) {

            log.warn("Ödəniş üçün sifariş tapılmadı. orderId={}", orderId);

            return "redirect:/api/orders/cart";
        }

        /*Hər ödəniş cəhdi üçün yeni unikal transactionId yaradılır.
         * Bu ID Order cədvəlində saxlanılır.
         * Sonra Portmanat callback göndərəndə
         * həmin transactionId vasitəsilə sifariş tapılır.
         */
        String transactionId =
                "TXN"
                        + System.currentTimeMillis()
                        + "-"
                        + UUID.randomUUID().toString().substring(0, 8);

        /*
         * Transaction ID-ni sifarişə bağlayırıq.
         *
         * attachTransactionId() EntityNotFoundException ata bilər.
         * Buna görə ayrıca try-catch istifadə olunur.
         */
        try {

            orderService.attachTransactionId(
                    orderId,
                    transactionId
            );

        } catch (EntityNotFoundException e) {

            log.error(
                    "TransactionId sifarişə bağlana bilmədi. orderId={}",
                    orderId
            );

            return "redirect:/api/orders/cart";
        }

        /*
         * Description URL üçün encode edilir.
         *
         * Boşluq və Azərbaycan simvolları problem yaratmasın.
         */
        String description = URLEncoder.encode(
                "Sifariş #" + orderId,
                StandardCharsets.UTF_8
        );

        /*
         * Demo Portmanat URL-i.
         *
         * Real inteqrasiyada Portmanat sənədlərindəki
         * bütün parametrlər və signature əlavə olunacaq.
         */
        String paymentUrl =
                "https://www.portmanat.az/az/pay"
                        + "?amount=" + order.getTotalAmount()
                        + "&description=" + description
                        + "&transactionId=" + transactionId
                        + "&merchantId=" + merchantId;

        return "redirect:" + paymentUrl;
    }

    /*
     * Ödəniş uğurlu olduqda göstərilən səhifə.
     *
     * PaymentController callback-dən buraya redirect edir.
     */
    @GetMapping("/checkout/success")
    public String checkoutSuccess(
            @RequestParam(value = "transactionId", required = false)
            String transactionId,

            Model model
    ) {

        model.addAttribute(
                "transactionId",
                transactionId
        );

        return "admin/checkout/success";
    }

    /*
     * Ödəniş uğursuz olduqda göstərilən səhifə.
     *
     * PaymentController callback-dən buraya redirect edir.
     */
    @GetMapping("/checkout/failure")
    public String checkoutFailure(
            @RequestParam(value = "transactionId", required = false)
            String transactionId,

            Model model
    ) {

        model.addAttribute(
                "transactionId",
                transactionId
        );

        return "admin/checkout/failure";
    }
}