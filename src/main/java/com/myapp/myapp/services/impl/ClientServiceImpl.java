package com.myapp.myapp.services.impl;

import com.myapp.myapp.dtos.ClientDtos.ClientCreateDto;
import com.myapp.myapp.dtos.ClientDtos.ClientDto;
import com.myapp.myapp.dtos.ClientDtos.ClientUpDateDto;
import com.myapp.myapp.models.Client;
import com.myapp.myapp.repositories.ClientRepository;
import com.myapp.myapp.services.ClientService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // Bu annotasiya Spring'ə bu sinfin biznes məntiqi daşıyan bir xidmət sinfi olduğunu bildirir.
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;

    // Konstruktor: Spring avtomatik olaraq bu obyektləri daxil edir (Dependency Injection).
    public ClientServiceImpl(ClientRepository clientRepository, ModelMapper modelMapper) {
        this.clientRepository = clientRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ClientDto> getAllClients() {
        // Bütün müştəriləri verilənlər bazasından gətirir.
        List<Client> clients = clientRepository.findAll();
        // Gətirilən Client obyektlərini ClientDto obyektlərinə çevirir.
        List<ClientDto> clientDtos = clients.stream().map(x -> modelMapper.map(x, ClientDto.class)).toList();
        return clientDtos;
    }

    @Override
    public ClientDto getClientsId(Long id) {
        // ID-yə görə müştərini tapır. Tapmasa xəta atır.
        Client client = clientRepository.findById(id).orElseThrow();
        // Tapılan Client obyektini ClientDto obyektinə çevirir.
        return modelMapper.map(client, ClientDto.class);
    }

    @Override
    public boolean createClients(ClientCreateDto clientCreateDto) {
        // Yeni ClientCreateDto obyektini Client obyektinə çevirir.
        Client client = modelMapper.map(clientCreateDto, Client.class);
        // Yeni müştəri məlumatını verilənlər bazasına yadda saxlayır.
        clientRepository.save(client);
        return true;
    }

    @Override
    public boolean updateClients(ClientUpDateDto clientUpDateDto, Long id) {
        // Yenilənəcək müştərinin ID-sini tapır.
        Optional<Client> optionalClient = clientRepository.findById(id);
        // Əgər müştəri tapılıbsa, məlumatları yeniləyir.
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            // ClientUpDateDto obyektinin məlumatlarını mövcud Client obyektinə köçürür.
            modelMapper.map(clientUpDateDto, client);
            // Yenilənmiş məlumatı verilənlər bazasına yadda saxlayır.
            clientRepository.save(client);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteClients(Long id) {
        // DÜZƏLİŞ: Silməzdən əvvəl ID-nin mövcudluğunu yoxlayırıq.
        if (clientRepository.existsById(id)) {
            clientRepository.deleteById(id);
            return true; // Uğurlu silinmə
        }
        return false; // Müştəri tapılmadı
    }

    @Override
    public ClientUpDateDto findClientById(Long id) {
        // ID-yə görə müştərini tapır və yeniləmə forması üçün DTO-ya çevirir.
        Client client = clientRepository.findById(id).orElseThrow();
        return modelMapper.map(client, ClientUpDateDto.class);
    }

    // Yeni əlavə etdiyimiz axtarış metodu.
    @Override
    public List<ClientDto> searchClients(String keyword) {
        // Boşluqları silir və axtarış sözünü kiçik hərflərə çevirir ki, axtarış düzgün işləsin.
        String trimmedKeyword = keyword.trim().toLowerCase();

        // Repository-nin metodunu çağırır və verilənlər bazasında axtarış edir.
        // Axtarış adı, email və ya təsviri əhatə edir.
        List<Client> clients = clientRepository.findByNameContainingOrEmailContainingOrDescriptionContaining(trimmedKeyword, trimmedKeyword, trimmedKeyword);

        // Axtarış nəticəsində tapılan Client obyektlərini ClientDto siyahısına çevirir.
        List<ClientDto> clientDtos = clients.stream()
                .map(client -> modelMapper.map(client, ClientDto.class))
                .collect(Collectors.toList());

        return clientDtos;
    }

    // YENİ METOD: Müştərilərin ümumi sayını gətirir (Dynamic Clients Counter üçün)
    @Override
    public long countClients() {
        // ClientRepository interfeysində avtomatik olaraq təmin olunan count() metodunu çağırır.
        return clientRepository.count();
    }
}