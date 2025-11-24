package com.myapp.myapp.controllers;

import com.myapp.myapp.dtos.ReservationDTO;
import com.myapp.myapp.models.Reservation;
import com.myapp.myapp.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody; // Mətn (String) cavabı üçün vacibdir

import jakarta.validation.Valid;

@Controller
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     * Rezervasiya (book-a-table) formundan gələn AJAX POST sorğusunu emal edir.
     * @ResponseBody ilə səhifə yönləndirməsi əvəzinə birbaşa mətn cavabı qaytarır.
     */
    @PostMapping("/book-a-table")
    @ResponseBody // <--- ƏSAS DÜZƏLİŞ BURADADIR
    public String processReservation(
            // AJAX JSON yox, Form Data göndərirsə, @ModelAttribute qalır
            @Valid @ModelAttribute("reservationDTO") ReservationDTO reservationDTO,
            BindingResult bindingResult
            // RedirectAttributes yığışdırılır (AJAX üçün lazım deyil)
    ) {
        // 1. Validasiya Xətalarının Yoxlanılması
        if (bindingResult.hasErrors()) {
            System.err.println("❌ Rezervasiya formunda doğrulama xətası aşkar edildi.");

            // Xəta mesajını Frontend'in başa düşəcəyi String formatında qaytarırıq
            return "ERROR: Zəhmət olmasa, formadakı səhvləri düzəldin və yenidən cəhd edin.";
        }

        try {
            // 2. DTO-nu Entity-yə çeviririk
            Reservation reservation = mapDtoToEntity(reservationDTO);

            // 3. Servis vasitəsilə rezervasiyanı yadda saxlayırıq
            reservationService.saveReservation(reservation);

            // 4. Uğurlu cavabı qaytarırıq. Frontend bunu uğurlu mesaj kimi görəcək.
            // "OK" və ya "SUCCESS: ..." formatı Yummy template-də uğur qəbul edilir.
            return "SUCCESS: Rezervasiyanız uğurla qəbul edildi. Təşəkkür edirik!";

        } catch (Exception e) {
            // 5. Server xətası baş verərsə
            System.err.println("❌ Rezervasiya emalı zamanı gözlənilməyən server xətası: " + e.getMessage());
            e.printStackTrace();
            return "ERROR: Serverdə gözlənilməyən xəta baş verdi. Zəhmət olmasa, yenidən cəhd edin.";
        }
    }

    /**
     * ReservationDTO-nu Reservation Entity-sinə çevirir (Köhnə metod olduğu kimi qalır).
     */
    private Reservation mapDtoToEntity(ReservationDTO dto) {
        Reservation entity = new Reservation();
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setReservationDate(dto.getDate());
        entity.setReservationTime(dto.getTime());
        entity.setPeople(dto.getPeople());
        entity.setMessage(dto.getMessage());
        return entity;
    }
}