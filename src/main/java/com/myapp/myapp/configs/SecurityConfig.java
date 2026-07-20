package com.myapp.myapp.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1: CSRF Qorumasının Tənzimlənməsi
                .csrf(csrf -> csrf
                        // DÜZƏLDİLDİ: Müştərinin POST etdiyi API yolları da bura əlavə olundu.
                        // Əgər /api/orders ignore edilməsəydi, front-end-dən sifariş keçəndə 403 Forbidden xətası verərdi.
                        .ignoringRequestMatchers(
                                "/contact",
                                "/book-a-table",
                                "/api/orders",
                                "/api/orders/create-payment"
                        )
                        // Qalan POST sorğuları (məsələn, admin login paneli) üçün CSRF tokeni kukidə saxlayır.
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )

                // 2: Keçid İcazələri (Sıralama Çox Vacibdir!)
                .authorizeHttpRequests(auth -> auth
                        // Sifariş Et, Əlaqə və API sifariş POST sorğularına hər kəs üçün icazə verilir
                        .requestMatchers(HttpMethod.POST, "/contact").permitAll()
                        .requestMatchers(HttpMethod.POST, "/book-a-table").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/orders").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/orders/create-payment").permitAll()

                        // Müştəri öz səbətinə baxa bilər (Hər kəsə açıqdır)
                        // Qeyd: Bu qayda aşağıdakı /api/orders/** qaydasından mütləq ƏVVƏL gəlməlidir!
                        .requestMatchers(HttpMethod.GET, "/api/orders/cart").permitAll()

                        // DÜZƏLDİLDİ: Bütün sifarişlərin siyahısı və detalları yalnız ADMIN rolu olanlar üçün.
                        // '/*' yerinə '/**' yazıldı ki, alt linklər (məsələn: /api/orders/detail/5) tam qorunsun.
                        .requestMatchers(HttpMethod.GET, "/api/orders").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/orders/**").hasRole("ADMIN")

                        // DÜZƏLDİLDİ: /admin ilə başlayan bütün səhifələrə yalnız daxil olmuş ADMIN rolu olanlar girə bilər.
                        // Əvvəl .authenticated() idi, yəni gələcəkdə "USER" rolu əlavə etsən o da girə bilərdi. İndi tam bağlandı.
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Qalan bütün digər yollara (Ana səhifə, məhsullar, CSS, JS, şəkillər) icazə ver
                        .anyRequest().permitAll()
                )
                // 3: Giriş (Login) Tənzimləmələri
                .formLogin(form -> form
                        .loginPage("/login") // Custom login səhifəmizin yolu
                        .defaultSuccessUrl("/admin/products", true) // Login uğurlu olduqda yönləndiriləcək səhifə
                        .permitAll()
                )
                // 4: Çıxış (Logout) Tənzimləmələri
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/") // Çıxış etdikdən sonra ana səhifəyə göndərir
                        .permitAll()
                );
        return http.build();
    }

    /**
     * Parolların təhlükəsiz şəkildə heşlənməsi (şifrələnməsi) üçün BCrypt istifadə olunur.
     * Bizim yaratdığımız CustomUserDetailsService bazadan parolu yoxlayarkən bu beandan istifadə edəcək.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // =========================================================================
    // SİLİNDİ: @Bean public UserDetailsService userDetailsService() metodu.
    // Artıq koda sabit yazılmış admin istifadəçisinə ehtiyac yoxdur.
    // Spring Boot bizim yazdığımız CustomUserDetailsService sinfini avtomatik tanıyacaq.
    // =========================================================================
}