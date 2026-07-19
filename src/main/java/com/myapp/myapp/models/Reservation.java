package com.myapp.myapp.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

// JPA-ya deyir ki, bu class bazada bir cədvəldir
@Entity
@Table(name = "reservations")
public class Reservation {

    // Primary Key (Əsas açar)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Formdan gələn məlumatlar
    private String name;
    private String phone;
    private String email;

    // Rezervasiya tarixi
    private LocalDate reservationDate;

    // Rezervasiya saatı
    private LocalTime reservationTime;

    // Rezervasiyada iştirak edəcək insanların sayı
    private Integer people;

    // Müştərinin əlavə mesajı
    private String message;

    // Rezervasiyanın bazaya yazılma vaxtı
    private LocalDateTime createdAt;

    // Boş Konstruktor (JPA üçün vacibdir)
    public Reservation() {
    }

    // Məlumatları qəbul edən Konstruktor
    public Reservation(
            String name,
            String phone,
            String email,
            LocalDate reservationDate,
            LocalTime reservationTime,
            Integer people,
            String message
    ) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
        this.people = people;
        this.message = message;
    }

    // Entity bazaya ilk dəfə yazılmadan əvvəl avtomatik işləyir
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public LocalTime getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(LocalTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    public Integer getPeople() {
        return people;
    }

    public void setPeople(Integer people) {
        this.people = people;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}