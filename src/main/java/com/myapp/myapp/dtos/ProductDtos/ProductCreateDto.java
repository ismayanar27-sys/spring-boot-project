package com.myapp.myapp.dtos.ProductDtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data
public class ProductCreateDto {

    // Validasiyalar DTO-ya əlavə edildi
    @NotBlank(message = "Ətir adı boş qala bilməz.")
    @Size(min = 3, max = 100, message = "Ad 3-100 simvol olmalıdır.")
    private String name;

    @NotBlank(message = "Təsvir boş qala bilməz.")
    @Size(max = 500, message = "Təsvir 500 simvoldan çox olmamalıdır.")
    private String description;

    @NotNull(message = "Qiymət daxil edilməlidir.")
    @DecimalMin(value = "0.01", message = "Qiymət 0.01-dən çox olmalıdır.")
    private Double price;

    @NotBlank(message = "Brend adı boş qala bilməz.")
    private String brand;

    @NotNull(message = "Həcm daxil edilməlidir.")
    @Min(value = 1, message = "Həcm 1 ml-dən az ola bilməz.")
    private Integer volume;

    @NotBlank(message = "Qoxu/Tərkib boş qala bilməz.")
    private String scent;

    @NotBlank(message = "Kateqoriya seçilməlidir.")
    private String category;

    // Şəkil faylı DTO daxilindədir.
    // Controller-də bu sahənin boş olub-olmaması yoxlanılacaq.
    private MultipartFile image;
}
