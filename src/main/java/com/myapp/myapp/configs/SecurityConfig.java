package com.myapp.myapp.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class   SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // KRİTİK DÜZƏLİŞ 1: Session xətasını aradan qaldırır və Front-end POST formalarını idarə edir.
                .csrf(csrf -> csrf
                        // Front-end formalarının CSRF qorumasını bağlayır:
                        // /contact və /book-a-table formaları POST sorğusu ilə işlədiyi üçün mütləq ignore edilməlidir.
                        .ignoringRequestMatchers("/contact", "/book-a-table")
                        // Qalan POST sorğuları üçün (məsələn, /admin login) tokeni kukidə saxlayır.
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )

                .authorizeHttpRequests(auth -> auth
                        // KRİTİK DÜZƏLİŞ 2: Bu iki formanın hər kəs tərəfindən istifadə edilməsinə icazə verir.
                        // Sifariş Et (book-a-table) və Əlaqə (contact) POST sorğuları
                        .requestMatchers(HttpMethod.POST, "/contact").permitAll()
                        .requestMatchers(HttpMethod.POST, "/book-a-table").permitAll()

                        // Qayda 3: Admin ilə başlayan hər şeyə yalnız daxil olan istifadəçilər baxa bilər
                        .requestMatchers("/admin/**").authenticated()

                        // Qayda 4: Bütün digər yollara (frontend səhifələri, CSS/JS/şəkillər) icazə ver
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/admin/products", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );
        return http.build();
    }

    /**
     * Təhlükəsizlik səbəbiylə əslində bu bean BCryptPasswordEncoder olmalıdır,
     * lakin layihənizin hazırda {noop} formatında işləməsi üçün NoOpPasswordEncoder istifadə olunur.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // InMemoryUserDetailsManager sadə test məqsədləri üçün. Şifrə: 12345
        UserDetails admin = User.builder()
                .username("admin")
                .password("12345")
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }
}
