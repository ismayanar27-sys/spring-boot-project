package com.myapp.myapp.dtos.ClientDtos;

import lombok.Data; // Getter, setter və digər standart metodları avtomatik yaratmaq üçün Lombok annotasiyası.

@Data // Bu sinfin bütün sahələri üçün getter və setter metodlarını yaradır.
public class ClientCreateDto {
    private String name; // Müştərinin adı
    private String description; // Müştəri haqqında qısa təsvir
    private String photoUrl; // Müştərinin şəklinin URL-i
    private String information; // Əlavə məlumat
    private String email; // Müştərinin elektron poçt ünvanı
}
