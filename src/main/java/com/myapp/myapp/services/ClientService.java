package com.myapp.myapp.services;

import com.myapp.myapp.dtos.ClientDtos.ClientCreateDto;
import com.myapp.myapp.dtos.ClientDtos.ClientDto;
import com.myapp.myapp.dtos.ClientDtos.ClientUpDateDto;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

// Bu interface müştərilərlə bağlı biznes məntiqinin müqaviləsini təyin edir.
// "ClientServiceImpl" sinfi bu interface'i implement edir.
public interface ClientService {
    // Verilənlər bazasından bütün müştəriləri gətirir və ClientDto siyahısı şəklində qaytarır.
    List<ClientDto> getAllClients();

    // ID-yə görə müştərini tapır və ClientDto obyektini qaytarır.
    ClientDto getClientsId(Long id);

    // Yeni müştəri əlavə edir. Uğurlu olarsa true, əks halda false qaytarır.
    // Şəkil yüklənməsi üçün MultipartFile istifadə edir.
    boolean createClients(ClientCreateDto clientCreateDto, MultipartFile image);

    // Müştəri məlumatlarını yeniləyir.
    boolean updateClients(ClientUpDateDto clientUpDateDto, Long id, MultipartFile image);

    // ID-yə görə müştərini silir.
    boolean deleteClients(Long id);

    // ID-yə görə müştərini tapır və ClientUpDateDto obyektini qaytarır.
    ClientUpDateDto findClientById(Long id);
}
