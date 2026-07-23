package com.myapp.myapp.controllers;

import com.myapp.myapp.models.OrderStatus;
import com.myapp.myapp.models.ReservationStatus;
import com.myapp.myapp.services.ClientService;
import com.myapp.myapp.services.ContactService;
import com.myapp.myapp.services.OrderService;
import com.myapp.myapp.services.ProductService;
import com.myapp.myapp.services.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Admin panelində ümumi statistika (dashboard-un genişləndirilmiş versiyası) səhifəsini idarə edir.
 * Bütün əsas modullardan (məhsul, müştəri, rezervasiya, sifariş, əlaqə mesajları) toplu məlumat göstərir.
 */
@Controller
@RequestMapping("/admin/statistics")
@RequiredArgsConstructor
public class AdminStatisticsController {

    private final ProductService productService;
    private final ClientService clientService;
    private final ReservationService reservationService;
    private final OrderService orderService;
    private final ContactService contactService;

    @GetMapping
    public String showStatistics(Model model) {

        // Ümumi saylar
        model.addAttribute("productCount", productService.countProducts());
        model.addAttribute("clientCount", clientService.countClients());
        model.addAttribute("messageCount", contactService.countMessages());

        // Rezervasiya statusuna görə bölgü
        model.addAttribute("reservationTotal", reservationService.countReservations());
        model.addAttribute("reservationPending", reservationService.countReservationsByStatus(ReservationStatus.PENDING));
        model.addAttribute("reservationConfirmed", reservationService.countReservationsByStatus(ReservationStatus.CONFIRMED));
        model.addAttribute("reservationCancelled", reservationService.countReservationsByStatus(ReservationStatus.CANCELLED));
        model.addAttribute("reservationCompleted", reservationService.countReservationsByStatus(ReservationStatus.COMPLETED));

        // Sifariş statusuna görə bölgü
        model.addAttribute("orderTotal", orderService.countOrders());
        model.addAttribute("orderPending", orderService.countOrdersByStatus(OrderStatus.PENDING));
        model.addAttribute("orderPaid", orderService.countOrdersByStatus(OrderStatus.PAID));
        model.addAttribute("orderFailed", orderService.countOrdersByStatus(OrderStatus.FAILED));
        model.addAttribute("orderDelivered", orderService.countOrdersByStatus(OrderStatus.DELIVERED));
        model.addAttribute("orderCancelled", orderService.countOrdersByStatus(OrderStatus.CANCELLED));

        // Ümumi gəlir (yalnız PAID sifarişlər)
        model.addAttribute("totalRevenue", orderService.getTotalRevenue());

        return "admin/statistics/statistics";
    }
}