package com.myapp.myapp.controllers;

import com.myapp.myapp.dtos.OrderDto;
import com.myapp.myapp.services.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class OrderAdminController {

    private final OrderService orderService;

    public OrderAdminController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String getAllOrders(Model model) {
        List<OrderDto> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "admin/orders/orders";
    }

    @GetMapping("/{id}")
    public String getOrderDetails(@PathVariable Long id, Model model) {
        OrderDto order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        return "admin/orders/order-details";
    }

    @PostMapping("/{id}/update-status")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        orderService.updateOrderStatus(id, status);
        return "redirect:/admin/orders/" + id;
    }
}