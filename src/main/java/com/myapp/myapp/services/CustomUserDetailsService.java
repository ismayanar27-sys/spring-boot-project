package com.myapp.myapp.services;

import com.myapp.myapp.models.User;
import com.myapp.myapp.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @Service - Bu annotasiya Spring-ə bildirir ki, bu sinif bir servis komponentidir.
 * Spring Security avtomatik olaraq bu servisi tapacaq və istifadəçi girişini (Login)
 * idarə etmək üçün InMemory variantı əvəzinə bu sinfi işə salacaq.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Bizə verilənlər bazasından (Lokal və ya Neon) istifadəçini tapmaq üçün repozitoriya lazımdır
    private final UserRepository userRepository;

    /**
     * Constructor Injection:
     * Spring Boot işə düşəndə UserRepository-ni avtomatik olaraq bu servisə enjektə edir.
     * Bu yanaşma @Autowired yazmaqdan daha təhlükəsiz və peşəkar üsuldur (Best Practice).
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Bu metod Spring Security-nin əsas ürəyidir.
     * Login formundan daxil edilən 'username' buraya gəlir və bazada yoxlanılır.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. Bizim yaratdığımız UserRepository vasitəsilə istifadəçini bazadan axtarırıq
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("İstifadəçi tapılmadı: " + username));

        /**
         * 2. Əgər istifadəçi tapılarsa, Spring Security-nin daxili anlaya biləcəyi
         * 'UserDetails' obyektini öz modelimizdəki (User) məlumatlarla bəzəyib geri qaytarırıq.
         *
         * .roles(user.getRole()) -> Sənin modelində @Data olduğu üçün getRole() metodunu birbaşa oxuyur.
         * .password(user.getPassword()) -> Bazadakı BCrypt ilə heşlənmiş parolu Spring-ə ötürür.
         */
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole()) // Bazadan gələn rol: məsələn "ADMIN"
                .build();
    }
}