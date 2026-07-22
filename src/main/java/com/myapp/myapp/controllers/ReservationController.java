package com.myapp.myapp.controllers;

import com.myapp.myapp.dtos.ReservationDTO;
import com.myapp.myapp.models.Reservation;
import com.myapp.myapp.services.ReservationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     * Rezervasiya formasından gələn POST sorğusunu emal edir.
     */
    @PostMapping("/book-a-table")
    @ResponseBody
    public String processReservation(
            @Valid @ModelAttribute("reservationDTO")
            ReservationDTO reservationDTO,
            BindingResult bindingResult
    ) {

        // Validasiya xətalarını yoxlayır
        if (bindingResult.hasErrors()) {

            log.warn(
                    "Rezervasiya formasında validasiya xətası aşkar edildi."
            );

            return "ERROR: Zəhmət olmasa, formadakı səhvləri düzəldin və yenidən cəhd edin.";
        }

        try {

            // DTO-nu Entity obyektinə çevirir
            Reservation reservation = mapDtoToEntity(reservationDTO);

            // Rezervasiyanı yadda saxlayır
            boolean reservationSaved =
                    reservationService.saveReservation(reservation);

            // Service artıq boolean qaytarır.
            // false olduqda reservation qəbul etmir.
            // Məsələn: restaurant capacity dolu ola bilər.
            if (!reservationSaved) {

                log.warn(
                        "Rezervasiya qəbul edilmədi. date={}, time={}, people={}",
                        reservation.getReservationDate(),
                        reservation.getReservationTime(),
                        reservation.getPeople()
                );

                return "ERROR: Seçdiyiniz tarix və saat üçün kifayət qədər yer yoxdur və ya rezervasiya qəbul edilə bilmədi.";
            }

            return "SUCCESS: Rezervasiyanız uğurla qəbul edildi. Təşəkkür edirik!";

        } catch (Exception e) {

            log.error(
                    "Rezervasiya emalı zamanı gözlənilməyən xəta baş verdi.",
                    e
            );

            return "ERROR: Serverdə gözlənilməyən xəta baş verdi. Zəhmət olmasa, yenidən cəhd edin.";
        }
    }

    /**
     * ReservationDTO obyektini Reservation Entity obyektinə çevirir.
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