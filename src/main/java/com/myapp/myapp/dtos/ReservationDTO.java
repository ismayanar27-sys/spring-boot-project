package com.myapp.myapp.dtos;

import lombok.Data;

@Data
public class ReservationDTO {
    private String name;
    private String email;
    private String phone;
    private String date;
    private String time;
    private Integer people;
    private String message;
}