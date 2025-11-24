package com.myapp.myapp.controllers;

import com.myapp.myapp.dtos.OrderCreateDto;
import com.myapp.myapp.dtos.OrderDto;
import com.myapp.myapp.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseBody
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderCreateDto orderCreateDto) {
        OrderDto createdOrder = orderService.createOrder(orderCreateDto);
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
        OrderDto order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/cart")
    public String viewCart(Model model) {
        OrderDto order = orderService.getOrderById(1L);
        if (order != null) {
            model.addAttribute("order", order);
        } else {
            // Sifariş tapılmasa, boş səbət səhifəsini göstərir
            model.addAttribute("message", "Səbətinizdə hələ heç bir sifariş yoxdur.");
        }
        return "admin/checkout/cart";
    }

    @PostMapping("/create-payment")
    public String createPayment(@RequestParam("orderId") Long orderId) {
        OrderDto order = orderService.getOrderById(orderId);
        if (order == null) {
            // Sifariş tapılmasa, xəta səhifəsi əvəzinə səbətə yönləndirir
            return "redirect:/api/orders/cart";
        }
        // Portmanat ödəniş URL-i
        String paymentUrl = "https://www.portmanat.az/az/pay" +
                "?amount=" + order.getTotalAmount() +
                "&description=" + "Sifariş #" + orderId +
                "&transactionId=" + "TXN" + System.currentTimeMillis() +
                "&merchantId=" + "Sizin_Merchant_ID-niz";
        return "redirect:" + paymentUrl;
    }
}