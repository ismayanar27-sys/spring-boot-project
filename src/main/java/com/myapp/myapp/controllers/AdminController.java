package com.myapp.myapp.controllers;

import com.myapp.myapp.services.ClientService;
import com.myapp.myapp.services.ProductService;
import com.myapp.myapp.services.ReservationService;
import com.myapp.myapp.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final ClientService clientService;
    private final ReservationService reservationService;
    private final OrderService orderService;

    /**
     * Admin panelinin əsas dashboard səhifəsi.
     */
    @GetMapping("/admin")
    public String dashboard(Model model) {

        // Dashboard statistikaları
        model.addAttribute("productCount", productService.countProducts());
        model.addAttribute("clientCount", clientService.countClients());
        model.addAttribute("reservationCount", reservationService.countReservations());
        model.addAttribute("orderCount", orderService.countOrders());

        return "admin/dashboard";
    }
}