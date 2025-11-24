package com.myapp.myapp.dtos.ClientDtos;

import lombok.Data;

@Data
public class ClientDto {
    private  Long id;
    private String name;
    private String description;
    private String information;
    private String email;
}

