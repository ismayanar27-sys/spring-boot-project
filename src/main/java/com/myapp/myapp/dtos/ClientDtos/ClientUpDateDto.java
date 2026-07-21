package com.myapp.myapp.dtos.ClientDtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClientUpDateDto {
    private Long id;

    @NotBlank(message = "Müştərinin adı boş qala bilməz.")
    @Size(max = 100, message = "Müştərinin adı 100 simvoldan çox ola bilməz.")
    private String name;

    @Size(max = 1000, message = "Təsvir 1000 simvoldan çox ola bilməz.")
    private String description;

    @Size(max = 1000, message = "Məlumat 1000 simvoldan çox ola bilməz.")
    private String information;

    @NotBlank(message = "E-poçt ünvanı boş qala bilməz.")
    @Email(message = "Düzgün e-poçt formatı daxil edin.")
    private String email;
}