package com.myapp.myapp.services;

import com.myapp.myapp.dtos.ClientDtos.ClientCreateDto;
import com.myapp.myapp.dtos.ClientDtos.ClientDto;
import com.myapp.myapp.dtos.ClientDtos.ClientUpDateDto;
import java.util.List;

// Bu interface müştərilərlə bağlı biznes məntiqinin müqaviləsini təyin edir.
// "ClientServiceImpl" sinfi bu interface'i implement edir.
public interface ClientService {
    // Verilənlər bazasından bütün müştəriləri gətirir və ClientDto siyahısı şəklində qaytarır.
    List<ClientDto> getAllClients();

    // ID-yə görə müştərini tapır və ClientDto obyektini qaytarır.
    ClientDto getClientsId(Long id);

    // Yeni müştəri əlavə edir. Uğurlu olarsa true, əks halda false qaytarır.
    boolean createClients(ClientCreateDto clientCreateDto);

    // Müştəri məlumatlarını yeniləyir.
    boolean updateClients(ClientUpDateDto clientUpDateDto, Long id);

    // ID-yə görə müştərini silir.
    boolean deleteClients(Long id);

    // ID-yə görə müştərini tapır və ClientUpDateDto obyektini qaytarır.
    ClientUpDateDto findClientById(Long id);

    // Yeni axtarış metodu.
    List<ClientDto> searchClients(String keyword);

    // YENİ METOD: Müştərilərin ümumi sayını gətirir
    long countClients(); // 👈 Bu hissəni əlavə edin!
}