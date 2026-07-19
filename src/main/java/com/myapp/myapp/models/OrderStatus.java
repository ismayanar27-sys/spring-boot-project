package com.myapp.myapp.models;

/**

 Sifarişin mümkün statuslarını müəyyən edir.


 Bu enum sərbəst mətnlə status göndərilməsinin qarşısını alır.

 Yalnız burada təyin olunmuş statuslar qəbul edilir.
 */
public enum OrderStatus {

    PENDING("Hazırlanır"),
    PAID("Ödənildi"),
    FAILED("Uğursuz"),
    DELIVERED("Göndərildi"),
    CANCELLED("Ləğv edildi");

    // Frontend-də göstərilən Azərbaycan dilində status adı
    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**

     Azərbaycan dilindəki status adına uyğun enum dəyərini tapır.


     Məsələn:
     "Hazırlanır" -> PENDING



     Uyğun status tapılmadıqda null qaytarır.
     */
    public static OrderStatus fromLabel(String label) {

        if (label == null || label.isBlank()) {
            return null;
        }

        for (OrderStatus status : values()) {
            if (status.label.equalsIgnoreCase(label.trim())) {
                return status;
            }
        }

        return null;
    }
}