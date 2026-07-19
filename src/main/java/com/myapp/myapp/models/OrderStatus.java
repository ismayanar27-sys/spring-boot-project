package com.myapp.myapp.models;

/**
 * Sifarişin mümkün statusları. Bunun məqsədi - updateOrderStatus() metoduna
 * istənilən sərbəst mətnin (məsələn "SALAM") yazılmasının qarşısını almaqdır.
 * Yalnız bu enum-da təyin olunan dəyərlər qəbul edilir.
 */
public enum OrderStatus {
    PENDING("Hazırlanır"),
    PAID("Ödənildi"),
    FAILED("Uğursuz"),
    DELIVERED("Göndərildi"),
    CANCELLED("Ləğv edildi");

    private final String label; // Azərbaycanca göstərilən ad (frontend-də görünən mətn)

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Admin panelinin HTML formu (order-details.html) status dəyərini
     * enum adı ("PENDING") yox, birbaşa Azərbaycanca mətn ("Hazırlanır") kimi göndərir.
     * Bu metod, mətndən uyğun enum-u tapır. Tapmasa null qaytarır.
     */
    public static OrderStatus fromLabel(String label) {
        for (OrderStatus s : values()) {
            if (s.label.equalsIgnoreCase(label)) {
                return s;
            }
        }
        return null;
    }
}