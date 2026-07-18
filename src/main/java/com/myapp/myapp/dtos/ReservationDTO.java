package com.myapp.myapp.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationDTO {

    @NotBlank(message = "Ad boş qala bilməz.")
    private String name;

    @NotBlank(message = "Email boş qala bilməz.")
    @Email(message = "Düzgün email formatı daxil edin.")
    private String email;

    @NotBlank(message = "Telefon nömrəsi boş qala bilməz.")
    private String phone;

    @NotBlank(message = "Tarix seçilməlidir.")
    private String date;

    @NotBlank(message = "Saat seçilməlidir.")
    private String time;

    @NotNull(message = "Nəfər sayı daxil edilməlidir.")
    @Min(value = 1, message = "Nəfər sayı ən azı 1 olmalıdır.")
    private Integer people;

    private String message;
}