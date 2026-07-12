package com.myapp.myapp.controllers;

import com.myapp.myapp.dtos.OrderCreateDto;
import com.myapp.myapp.dtos.OrderDto;
import com.myapp.myapp.services.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private static final String CART_SESSION_KEY = "CURRENT_ORDER_ID";

    private final OrderService orderService;

    // application.properties-dən oxunur: portmanat.merchant-id=${PORTMANAT_MERCHANT_ID}
    @Value("${portmanat.merchant-id:}")
    private String merchantId;

    @PostMapping
    @ResponseBody
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderCreateDto orderCreateDto, HttpSession session) {
        OrderDto createdOrder = orderService.createOrder(orderCreateDto);
        // Yeni sifariş yaradılanda, ID-ni bu istifadəçinin sessiyasında saxlayırıq.
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
        OrderDto order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        Long currentOrderId = (Long) session.getAttribute(CART_SESSION_KEY);
        OrderDto order = currentOrderId != null ? orderService.getOrderById(currentOrderId) : null;
        if (order != null) {
            model.addAttribute("order", order);
        } else {
            model.addAttribute("message", "Səbətinizdə hələ heç bir sifariş yoxdur.");
        }
        return "admin/checkout/cart";
    }

    @PostMapping("/create-payment")
    public String createPayment(@RequestParam("orderId") Long orderId) {
        OrderDto order = orderService.getOrderById(orderId);
        if (order == null) {
            return "redirect:/api/orders/cart";
        }
        // DİQQƏT: Bu, sadəcə demo redirect-dir - real Portmanat inteqrasiyası
        // adətən imza (signature/hash) və server-to-server callback tələb edir.
        String paymentUrl = "https://www.portmanat.az/az/pay" +
                "?amount=" + order.getTotalAmount() +
                "&description=" + "Sifariş #" + orderId +
                "&transactionId=" + "TXN" + System.currentTimeMillis() +
                "&merchantId=" + merchantId;
        return "redirect:" + paymentUrl;
    }
}