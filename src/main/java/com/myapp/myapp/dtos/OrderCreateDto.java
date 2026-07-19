package com.myapp.myapp.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreateDto {

    @NotBlank(message = "Müştərinin adı boş qala bilməz.")
    @Size(max = 100, message = "Müştərinin adı 100 simvoldan çox ola bilməz.")
    private String customerName;

    @NotBlank(message = "E-poçt ünvanı boş qala bilməz.")
    @Email(message = "Düzgün e-poçt formatı daxil edin.")
    private String customerEmail;

    @NotBlank(message = "Telefon nömrəsi boş qala bilməz.")
    @Size(max = 30, message = "Telefon nömrəsi 30 simvoldan çox ola bilməz.")
    private String customerPhone;

    @NotEmpty(message = "Sifarişdə ən azı bir məhsul olmalıdır.")
    @Valid
    private List<OrderItemDto> orderItems;

    @NotBlank(message = "Ödəniş üsulu seçilməlidir.")
    private String paymentMethod;

}