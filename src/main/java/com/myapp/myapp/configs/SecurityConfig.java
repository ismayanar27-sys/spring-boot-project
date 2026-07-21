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

                        // Ödəniş nəticə səhifələri (uğurlu/uğursuz) də hər kəsə
                        // açıq olmalıdır. Əks halda müştəri ödənişdən sonra bu səhifəyə
                        // yönləndiriləndə "/api/orders/**" qaydasına düşüb ADMIN login
                        // səhifəsinə atılardı - müştəri heç vaxt nəticəni görməzdi.
                        // Bu qaydalar da aşağıdakı /api/orders/** qaydasından ƏVVƏL gəlməlidir!
                        .requestMatchers(HttpMethod.GET, "/api/orders/checkout/success").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/orders/checkout/failure").permitAll()

                        // Bütün sifarişlərin siyahısı və detalları yalnız ADMIN rolu olanlar üçün.
                        // '/*' yerinə '/**' yazıldı ki, alt linklər (məsələn: /api/orders/detail/5) tam qorunsun
                        .requestMatchers(HttpMethod.GET, "/api/orders").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/orders/**").hasRole("ADMIN")

                        // /admin ilə başlayan bütün səhifələrə yalnız daxil olmuş ADMIN rolu olanlar girə bilər
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
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}