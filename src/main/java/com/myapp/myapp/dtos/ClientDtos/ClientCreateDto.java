package com.myapp.myapp.dtos.ClientDtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data; // Getter, setter və digər standart metodları avtomatik yaratmaq üçün Lombok annotasiyası.

@Data // Bu sinfin bütün sahələri üçün getter və setter metodlarını yaradır.
public class ClientCreateDto {

    @NotBlank(message = "Müştərinin adı boş qala bilməz.")
    @Size(max = 100, message = "Müştərinin adı 100 simvoldan çox ola bilməz.")
    private String name; // Müştərinin adı

    @Size(max = 1000, message = "Təsvir 1000 simvoldan çox ola bilməz.")
    private String description; // Müştəri haqqında qısa təsvir

    @Size(max = 1000, message = "Məlumat 1000 simvoldan çox ola bilməz.")
    private String information; // Əlavə məlumat

    @NotBlank(message = "E-poçt ünvanı boş qala bilməz.")
    @Email(message = "Düzgün e-poçt formatı daxil edin.")
    private String email; // Müştərinin elektron poçt ünvanı
}