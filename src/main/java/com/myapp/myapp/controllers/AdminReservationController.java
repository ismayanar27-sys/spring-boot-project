package com.myapp.myapp.controllers;

import com.myapp.myapp.models.Reservation;
import com.myapp.myapp.models.ReservationStatus;
import com.myapp.myapp.services.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/reservations")
@Slf4j
public class AdminReservationController {

    private final ReservationService reservationService;

    // Constructor Injection
    public AdminReservationController(
            ReservationService reservationService
    ) {
        this.reservationService = reservationService;
    }

    /**
     * Admin panelində bütün rezervasiyaları göstərir.
     */
    @GetMapping
    public String getAllReservations(Model model) {

        log.info("Bütün rezervasiyalar gətirilir.");

        model.addAttribute(
                "reservations",
                reservationService.getAllReservations()
        );

        return "admin/reservations/reservation-list";
    }
    /**
     * Seçilmiş rezervasiyanın bütün məlumatlarını göstərir.
     */
    @GetMapping("/{id}")
    public String getReservationDetails(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        Reservation reservation =
                reservationService.getReservationById(id);

        if (reservation == null) {

            log.warn(
                    "Rezervasiya tapılmadı. ID={}",
                    id
            );

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Rezervasiya tapılmadı."
            );

            return "redirect:/admin/reservations";
        }

        model.addAttribute(
                "reservation",
                reservation
        );

        return "admin/reservations/reservation";
    }

    /**
     * Statusa görə rezervasiyaları filtr edir.
     */
    @GetMapping("/status/{status}")
    public String getReservationsByStatus(
            @PathVariable ReservationStatus status,
            Model model
    ) {

        log.info(
                "Statusa görə rezervasiyalar gətirilir. Status={}",
                status
        );

        model.addAttribute(
                "reservations",
                reservationService.getReservationsByStatus(status)
        );

        model.addAttribute(
                "selectedStatus",
                status
        );

        return "admin/reservations/reservation-list";
    }
    /**
     * Admin rezervasiyanın statusunu dəyişə bilər.
     */
    @PostMapping("/{id}/status")
    public String updateReservationStatus(
            @PathVariable Long id,

            @RequestParam ReservationStatus status,

            RedirectAttributes redirectAttributes
    ) {

        boolean success =
                reservationService.updateReservationStatus(
                        id,
                        status
                );

        if (success) {

            log.info(
                    "Rezervasiya statusu yeniləndi. ID={}, Status={}",
                    id,
                    status
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Rezervasiya statusu uğurla yeniləndi."
            );

        } else {

            log.warn(
                    "Rezervasiya statusu yenilənmədi. ID={}",
                    id
            );

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Rezervasiya statusu yenilənə bilmədi."
            );
        }

        return "redirect:/admin/reservations";
    }
}