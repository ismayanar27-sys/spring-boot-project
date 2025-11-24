package com.myapp.myapp.dtos.ProductDtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class ProductUpdateDto {

    private Long id;

    // Məhsul adı boş ola bilməz və minimum 3 simvol olmalıdır
    @NotBlank(message = "Ad sahəsi boş ola bilməz.")
    @Size(min = 3, max = 255, message = "Ad minimum 3, maksimum 255 simvol olmalıdır.")
    private String name;

    // Təsvir boş ola bilməz və minimum 10 simvol olmalıdır
    @NotBlank(message = "Təsvir sahəsi boş ola bilməz.")
    @Size(min = 10, max = 1000, message = "Təsvir minimum 10, maksimum 1000 simvol olmalıdır.")
    private String description;

    // Qiymət boş ola bilməz və minimum 0.01 olmalıdır
    @NotNull(message = "Qiymət sahəsi boş ola bilməz.")
    @DecimalMin(value = "0.01", message = "Qiymət ən azı 0.01 olmalıdır.")
    private BigDecimal price;

    // Brend adı boş ola bilməz
    @NotBlank(message = "Brend adı boş ola bilməz.")
    private String brand;

    // Kateqoriya boş ola bilməz
    @NotBlank(message = "Kateqoriya seçilməlidir.")
    private String category;

    // Həcm boş ola bilməz və minimum 1 olmalıdır
    @NotNull(message = "Həcm sahəsi boş ola bilməz.")
    @Min(value = 1, message = "Həcm ən azı 1 ml olmalıdır.")
    private Integer volume;

    // Qoxu/Tərkib boş ola bilməz
    @NotBlank(message = "Qoxu/Tərkib sahəsi boş ola bilməz.")
    private String scent;

    private String photoUrl; // Köhnə şəkil URL-i
}
