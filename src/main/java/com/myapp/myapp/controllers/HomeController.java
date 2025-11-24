package com.myapp.myapp.controllers;

import com.myapp.myapp.dtos.ReservationDTO;
import com.myapp.myapp.services.ClientService;
import com.myapp.myapp.services.ReservationService;
import com.myapp.myapp.services.ProductService; // ✅ Məhsul sayını almaq üçün yeni Service import edildi
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ClientService clientService;
    private final ReservationService reservationService;
    private final ProductService productService; // ✅ ProductService dəyişən kimi əlavə edildi

    // Constructor Injection: Spring bütün lazımi Service obyektlərini avtomatik ötürür.
    public HomeController(ClientService clientService, ReservationService reservationService, ProductService productService) {
        this.clientService = clientService;
        this.reservationService = reservationService;
        this.productService = productService; // ✅ ProductService mənimsədildi
    }

    // Ana səhifəni yükləyir. URL: /, /index
    @GetMapping({"/", "/index"})
    public String home(Model model) {

        // --- DİNAMİK SAYĞACLAR (STATS) ÜÇÜN MƏLUMAT TOPLANMASI ---

        // 1. Clients Sayı: Bazadakı ümumi müştəri sayını gətirir.
        long clientCount = clientService.countClients();
        model.addAttribute("clientCount", clientCount);

        // 2. Reservations/Orders Sayı: Bazadakı ümumi rezervasiya/sifariş sayını gətirir.
        long projectCount = reservationService.countReservations();
        model.addAttribute("projectCount", projectCount); // HTML-də 'projectCount' adı ilə istifadə olunur.

        // 3. Products Sayı: Ətir Mağazası üçün ümumi məhsul sayını gətirir.
        long workerCount = productService.countProducts();
        model.addAttribute("workerCount", workerCount); // HTML-də 'workerCount' adı ilə istifadə olunur.

        // --- DİGƏR MƏLUMATLAR ---

        // Rezervasiya forması üçün boş DTO obyektini Modelə əlavə edir.
        if (!model.containsAttribute("reservationDTO")) {
            model.addAttribute("reservationDTO", new ReservationDTO());
        }

        // Thymeleaf şablonunun yerini göstərir.
        return "front/index";
    }
}