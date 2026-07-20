package com.myapp.myapp.dtos.ProductDtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateDto {

    private Long id;

    @NotBlank(message = "Məhsul adı boş qala bilməz.")
    @Size(min = 3, max = 100, message = "Ad 3-100 simvol olmalıdır.")
    private String name;

    @NotBlank(message = "Təsvir boş qala bilməz.")
    @Size(max = 1000, message = "Təsvir 1000 simvoldan çox olmamalıdır.")
    private String description;

    @NotNull(message = "Qiymət daxil edilməlidir.")
    @DecimalMin(value = "0.01", message = "Qiymət 0.01-dən çox olmalıdır.")
    private BigDecimal price;

    @NotNull(message = "Hazırlanma vaxtı daxil edilməlidir.")
    @Min(value = 1, message = "Hazırlanma vaxtı ən azı 1 dəqiqə olmalıdır.")
    private Integer preparationTime;

    @NotBlank(message = "Kateqoriya seçilməlidir.")
    private String category;

    /*
     * SİLİNDİ:
     * photoUrl
     *
     * Köhnə şəkil URL-i DTO-dan idarə edilməməlidir.
     * Şəkil dəyişdirilmədikdə mövcud photoUrl Service-də saxlanılır.
     * Şəkil dəyişdirildikdə yeni image Controller/Service vasitəsilə idarə olunur.
     */

}
