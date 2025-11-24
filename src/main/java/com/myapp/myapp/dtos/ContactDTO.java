package com.myapp.myapp.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContactDTO {

    // Ad/Soyad boş olmamalıdır
    @NotBlank(message = "Ad/Soyad boş qala bilməz")
    private String name;

    // Email həm boş olmamalı, həm də düzgün formatda olmalıdır
    @NotBlank(message = "Email daxil edilməlidir")
    @Email(message = "Düzgün email formatı daxil edin")
    private String email;

    // Mövzu boş olmamalıdır
    @NotBlank(message = "Mövzu daxil edilməlidir")
    private String subject;

    // Mesaj boş olmamalıdır
    @NotBlank(message = "Mesaj boş qala bilməz")
    private String message;
}