package com.myapp.myapp.dtos.ProductDtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
public class ProductCreateDto {

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

    // Şəkil faylı Controller-də ayrıca yoxlanılır.
    private MultipartFile image;

}
