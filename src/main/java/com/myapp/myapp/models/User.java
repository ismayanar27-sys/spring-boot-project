package com.myapp.myapp.models;

import jakarta.persistence.*;
import lombok.Data;

/**
 * @Entity - Bu sinfin verilənlər bazasında bir cədvələ qarşılıq gəldiyini Spring Data JPA-ya bildirir.
 * @Table(name = "users") - Baza daxilində yaradılacaq cədvəlin adını xüsusi olaraq "users" təyin edir.
 * @Data - Lombok kitabxanasına məxsusdur. Arxa planda bizim üçün Getter, Setter, toString(),
 *         equals() və hashCode() metodlarını avtomatik olaraq generasiya edir (kodu təmiz saxlayır).
 */
@Entity
@Table(name = "users")
@Data
public class User {

    /**
     * @Id - Bu sahənin cədvəlin "Primary Key"i (əsas açarı) olduğunu bildirir.
     * @GeneratedValue - İdentifikatorun avtomatik artırılma strategiyasını təyin edir.
     * IDENTITY - PostgreSQL və Neon-da "SERIAL" (auto-increment) məntiqi ilə işləyir.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @Column - Sahənin cədvəldəki sütun xüsusiyyətlərini təyin edir.
     * nullable = false - İstifadəçi adı boş (null) buraxıla bilməz.
     * unique = true - Bazada eyni istifadəçi adından ikincisi ola bilməz (təkrarlanmanın qarşısını alır).
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * password - İstifadəçinin şifrəsini saxlayır.
     * nullable = false - Şifrə sahəsi boş qala bilməz.
     * Qeyd: Bura daxil ediləcək şifrə mütləq BCrypt ilə heşlənmiş (şifrələnmiş) formada olmalıdır.
     */
    @Column(nullable = false)
    private String password;

    /**
     * role - İstifadəçinin sistemdəki səlahiyyətini (rolunu) təyin edir.
     * nullable = false - Rol sahəsi boş qala bilməz.
     * Spring Security ilə tam uyğun işləməsi üçün bura "ADMIN" və ya "USER" kimi dəyərlər yazılacaq.
     */
    @Column(nullable = false)
    private String role;
}