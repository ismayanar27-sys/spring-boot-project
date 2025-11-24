package com.myapp.myapp.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

// JPA-ya deyir ki, bu, bazada bir cədvəldir
@Entity
@Table(name = "reservations")
public class Reservation {

    // Primary Key (Əsas açar)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Formdan gələn sahələr
    private String name;
    private String phone;
    private String email;
    private String reservationDate;
    private String reservationTime;
    private Integer people; // DÜZƏLİŞ: int əvəzinə Integer istifadə edildi
    private String message;

    // Sifarişin bazaya yazılma vaxtı
    private LocalDateTime createdAt = LocalDateTime.now();

    // Boş Konstruktor (JPA üçün vacibdir)
    public Reservation() {
    }

    // Məlumatları qəbul edən Konstruktor
    // DÜZƏLİŞ: int əvəzinə Integer istifadə edildi
    public Reservation(String name, String phone, String email, String reservationDate, String reservationTime, Integer people, String message) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
        this.people = people;
        this.message = message;
    }

    // Getter və Setterlər
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getReservationDate() { return reservationDate; }
    public void setReservationDate(String reservationDate) { this.reservationDate = reservationDate; }
    public String getReservationTime() { return reservationTime; }
    public void setReservationTime(String reservationTime) { this.reservationTime = reservationTime; }

    // DÜZƏLİŞ: int əvəzinə Integer istifadə edildi
    public Integer getPeople() { return people; }
    public void setPeople(Integer people) { this.people = people; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}