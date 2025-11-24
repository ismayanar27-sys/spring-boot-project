package com.myapp.myapp.controllers;

import com.myapp.myapp.dtos.ClientDtos.ClientCreateDto;
import com.myapp.myapp.dtos.ClientDtos.ClientDto;
import com.myapp.myapp.dtos.ClientDtos.ClientUpDateDto;
import com.myapp.myapp.services.ClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    // Əsas müştərilər siyahısı və axtarış nəticələrini göstərmək üçün metod
    @GetMapping
    public String getAllClients(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<ClientDto> clients;
        // Əgər axtarış sözü (keyword) boş deyilsə, axtarış funksiyasını işə sal.
        if (keyword != null && !keyword.isEmpty()) {
            clients = clientService.searchClients(keyword);
            // Frontend-də axtarış inputunun dəyərini saxlamaq üçün əlavə edirik.
            model.addAttribute("keyword", keyword);
        } else {
            // Əks halda, bütün müştərilərin siyahısını göstər.
            clients = clientService.getAllClients();
        }
        model.addAttribute("clients", clients);
        return "admin/clients/clients-list";
    }

    @GetMapping("/{id}")
    public String getClientById(@PathVariable Long id, Model model) {
        ClientDto client = clientService.getClientsId(id);
        model.addAttribute("client", client);
        return "admin/clients/client-details";
    }

    @GetMapping("/add")
    public String addClientForm(Model model) {
        model.addAttribute("clientCreateDto", new ClientCreateDto());
        return "admin/clients/client-add";
    }

    @PostMapping("/add")
    public String addClient(@ModelAttribute ClientCreateDto clientCreateDto,
                            RedirectAttributes redirectAttributes) {
        clientService.createClients(clientCreateDto);
        redirectAttributes.addFlashAttribute("successMessage", "Müştəri uğurla əlavə edildi!");
        return "redirect:/admin/clients";
    }

    @GetMapping("/edit/{id}")
    public String editClientForm(@PathVariable Long id, Model model) {
        ClientUpDateDto clientUpDateDto = clientService.findClientById(id);
        model.addAttribute("clientUpDateDto", clientUpDateDto);
        return "admin/clients/client-update";
    }

    @PostMapping("/update/{id}")
    public String updateClient(@PathVariable Long id,
                               @ModelAttribute ClientUpDateDto clientUpDateDto,
                               RedirectAttributes redirectAttributes) {
        clientService.updateClients(clientUpDateDto, id);
        redirectAttributes.addFlashAttribute("successMessage", "Müştəri uğurla yeniləndi!");
        return "redirect:/admin/clients";
    }

    @PostMapping("/delete/{id}")
    public String deleteClient(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        clientService.deleteClients(id);
        redirectAttributes.addFlashAttribute("successMessage", "Müştəri uğurla silindi!");
        return "redirect:/admin/clients";
    }
}