package com.myapp.myapp.services;

import com.myapp.myapp.models.Reservation;
import com.myapp.myapp.models.ReservationStatus;

import java.util.List;

public interface ReservationService {

    /* Reservation yalnız bazaya yazılmır
     * Tarix + saat və restaurant capacity yoxlamaları
     * Service qatında aparılır
     * Buna görə əməliyyatın uğurlu olub-olmadığını
     * Controller-ə qaytarmaq üçün boolean istifadə olunur */

    // Rezervasiyanı validation və capacity yoxlamasından keçirərək yadda saxlayır.
    boolean saveReservation(Reservation reservation);

    // Aktiv rezervasiyaların ümumi sayını gətirir (Projects sayğacı üçün)
    long countReservations();

    // Bütün rezervasiyaları ən yeni tarixə görə qaytarır
    List<Reservation> getAllReservations();

    // ID-yə görə rezervasiya tapır
    Reservation getReservationById(Long id);

    // Statusa görə rezervasiyaları filtr edir
    List<Reservation> getReservationsByStatus(ReservationStatus status);

    // Admin rezervasiyanın statusunu dəyişə bilir
    boolean updateReservationStatus(Long id, ReservationStatus status);


    long countReservationsByStatus(ReservationStatus reservationStatus);
}