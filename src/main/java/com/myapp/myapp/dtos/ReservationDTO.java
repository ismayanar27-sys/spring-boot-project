package com.myapp.myapp.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ReservationDTO {

    @NotBlank(message = "Ad boş qala bilməz.")
    @Size(max = 100, message = "Ad 100 simvoldan çox ola bilməz.")
    private String name;

    @NotBlank(message = "Email boş qala bilməz.")
    @Email(message = "Düzgün email formatı daxil edin.")
    private String email;

    @NotBlank(message = "Telefon nömrəsi boş qala bilməz.")
    @Size(max = 30, message = "Telefon nömrəsi 30 simvoldan çox ola bilməz.")
    private String phone;

    @NotNull(message = "Tarix seçilməlidir.")

    /*
     * SİLİNDİ: @PastOrPresent
     *
     * SƏBƏB:
     * @PastOrPresent yalnız LocalDate-i yoxlayırdı.
     * LocalDate və LocalTime birlikdə yoxlanılmadığı üçün
     * bu günün keçmiş saatına reservation etmək mümkün idi.
     *
     * Tarix + saat yoxlaması Service qatında birlikdə aparılacaq.
     */
    private LocalDate date;

    @NotNull(message = "Saat seçilməlidir.")
    private LocalTime time;

    @NotNull(message = "Nəfər sayı daxil edilməlidir.")
    @Min(value = 1, message = "Nəfər sayı ən azı 1 olmalıdır.")
    private Integer people;

    @Size(max = 500, message = "Mesaj 500 simvoldan çox ola bilməz.")
    private String message;
}