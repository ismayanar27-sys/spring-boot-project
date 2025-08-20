package com.myapp.myapp.controllers;

import com.myapp.myapp.dtos.ClientDtos.ClientCreateDto;
import com.myapp.myapp.dtos.ClientDtos.ClientDto;
import com.myapp.myapp.dtos.ClientDtos.ClientUpDateDto;
import com.myapp.myapp.services.ClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller // Spring'e bu sinfin bir web kontroller olduğunu bildirir.
@RequestMapping("/clients") // Bu kontrollerdeki bütün URL-ler "/clients" ile başlayacaq.
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping // "/clients" URL-ine gelen GET sorğularını emal edir.
    public String getAllClients(Model model) {
        List<ClientDto> clients = clientService.getAllClients();
        model.addAttribute("clients", clients);
        return "clients-list"; // "templates/clients-list.html" sehifesini gösterir.
    }

    @GetMapping("/{id}") // Müştəri ID-si ile melumatları getirir.
    public String getClientById(@PathVariable Long id, Model model) {
        ClientDto client = clientService.getClientsId(id);
        model.addAttribute("client", client);
        return "client-details"; // "templates/client-details.html" sehifesini gösterir.
    }

    @GetMapping("/add") // Yeni müştəri elave etmek üçün formanı gösterir.
    public String addClientForm(Model model) {
        model.addAttribute("clientCreateDto", new ClientCreateDto());
        return "client-add"; // "templates/client-add.html" sehifesini gösterir.
    }

    @PostMapping("/add") // Yeni müştəri elave etmek üçün POST sorğusunu emal edir.
    public String addClient(@ModelAttribute ClientCreateDto clientCreateDto,
                            @RequestParam("image") MultipartFile image) {
        clientService.createClients(clientCreateDto, image);
        return "redirect:/clients"; // Müştəri siyahısı sehifesine yönlendirir.
    }

    @GetMapping("/update/{id}") // Müştəri melumatlarını yenilemek üçün formanı gösterir.
    public String updateClientForm(@PathVariable Long id, Model model) {
        ClientUpDateDto clientUpDateDto = clientService.findClientById(id);
        model.addAttribute("clientUpDateDto", clientUpDateDto);
        return "client-update"; // "templates/client-update.html" sehifesini gösterir.
    }

    @PostMapping("/update/{id}") // Müştəri melumatlarını yenilemek üçün POST sorğusunu emal edir.
    public String updateClient(@PathVariable Long id,
                               @ModelAttribute ClientUpDateDto clientUpDateDto,
                               @RequestParam("image") MultipartFile image) {
        clientService.updateClients(clientUpDateDto, id, image);
        return "redirect:/clients"; // Müştəri siyahısı sehifesine yönlendirir.
    }
    @PostMapping("/delete/{id}") // Müştərini silmək üçün POST sorğusunu emal edir.
    public String deleteClient(@PathVariable Long id) {
        clientService.deleteClients(id);
        return "redirect:/clients"; // Müştəri siyahısı səhifəsinə yönləndirir.
    }
    }

