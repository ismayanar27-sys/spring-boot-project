package com.myapp.myapp.models;

import jakarta.persistence.*; // JPA (Java Persistence API) annotasiyaları
import lombok.Data; // Lombok annotasiyası, getter və setter metodlarını avtomatik yaradır.

@Entity // Bu sinfin JPA entity-si olduğunu, yəni verilənlər bazasındakı cədvəli təmsil etdiyini bildirir.
@Table(name="clients") // Verilənlər bazasındakı cədvəlin adını "clients" olaraq təyin edir.
@Data // Bu sinfin bütün sahələri üçün getter və setter metodlarını avtomatik yaradır.
public class Client {
        @Id // Bu sahənin cədvəlin primary key (əsas açarı) olduğunu bildirir.
        @GeneratedValue(strategy = GenerationType.IDENTITY) // ID-nin avtomatik artırılmasını təmin edir.
        private  Long id;
        private String name; // Müştərinin adı.
        private String description; // Müştəri haqqında qısa təsvir.
        private String information; // Əlavə məlumat.
        @Column(unique = true) // Bu sahədəki dəyərlərin təkrarolunmaz olmasını təmin edir.
        private String email; // Müştərinin elektron poçt ünvanı.
}
