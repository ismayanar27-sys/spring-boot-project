package com.myapp.myapp.controllers;

import com.myapp.myapp.dtos.OrderDto;
import com.myapp.myapp.services.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
@Slf4j
public class OrderAdminController {

    private final OrderService orderService;

    public OrderAdminController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Bütün sifarişləri göstərir.
     */
    @GetMapping
    public String getAllOrders(Model model) {

        List<OrderDto> orders = orderService.getAllOrders();

        model.addAttribute("orders", orders);

        return "admin/orders/orders";
    }

    /**
     * Seçilmiş sifarişin detallarını göstərir.
     */
    @GetMapping("/{id}")
    public String getOrderDetails(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        try {

            OrderDto order = orderService.getOrderById(id);

            model.addAttribute("order", order);

            return "admin/orders/order-details";

        } catch (RuntimeException e) {

            log.error("Sifariş tapılmadı. ID={}", id, e);

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Sifariş tapılmadı."
            );

            return "redirect:/admin/orders";
        }
    }

    /**
     * Admin sifariş statusunu yeniləyir.
     */
    @PostMapping("/{id}/update-status")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status,
            RedirectAttributes redirectAttributes
    ) {

        try {

            orderService.updateOrderStatus(id, status);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Sifariş statusu uğurla yeniləndi."
            );

        } catch (IllegalArgumentException e) {

            log.warn(
                    "Sifariş statusu yenilənərkən xəta: {}",
                    e.getMessage()
            );

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );

        } catch (RuntimeException e) {

            log.error(
                    "Sifariş tapılmadı. ID={}",
                    id,
                    e
            );

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Sifariş tapılmadı."
            );
        }

        return "redirect:/admin/orders/" + id;
    }
}