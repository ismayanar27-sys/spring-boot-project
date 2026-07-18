package com.myapp.myapp.controllers;

import com.myapp.myapp.dtos.ClientDtos.ClientCreateDto;
import com.myapp.myapp.dtos.ClientDtos.ClientDto;
import com.myapp.myapp.dtos.ClientDtos.ClientUpDateDto;
import com.myapp.myapp.services.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/clients")
@Slf4j
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
    public String getClientById(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        // try-catch - musteri tapilmasa cirkin 500 sehifesi evezine
        // rahat mesajla siyahiya geri qaytarir
        try {
            ClientDto client = clientService.getClientsId(id);
            model.addAttribute("client", client);
            return "admin/clients/client-details";
        } catch (RuntimeException e) {
            log.error("Musteri tapilmadi ID: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Xəta: Müştəri ID-si (" + id + ") tapılmadı.");
            return "redirect:/admin/clients";
        }
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
    public String editClientForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        //try-catch - eyni menteqle
        try {
            ClientUpDateDto clientUpDateDto = clientService.findClientById(id);
            model.addAttribute("clientUpDateDto", clientUpDateDto);
            return "admin/clients/client-update";
        } catch (RuntimeException e) {
            log.error("Redakte ucun musteri tapilmadi ID: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Redaktə etmək istədiyiniz müştəri tapılmadı.");
            return "redirect:/admin/clients";
        }
    }

    @PostMapping("/update/{id}")
    public String updateClient(@PathVariable Long id,
                               @ModelAttribute ClientUpDateDto clientUpDateDto,
                               RedirectAttributes redirectAttributes) {
        //true/false nəticəsi yoxlanılır - əvvəl "uğurlu" yazılırdı, müştəri tapılmasa belə
        boolean updated = clientService.updateClients(clientUpDateDto, id);
        if (updated) {
            redirectAttributes.addFlashAttribute("successMessage", "Müştəri uğurla yeniləndi!");
        } else {
            log.warn("Musteri yenilenmedi, tapilmadi ID: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Yeniləmə uğursuz oldu: müştəri tapılmadı.");
        }
        return "redirect:/admin/clients";
    }

    @PostMapping("/delete/{id}")
    public String deleteClient(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        //eyni məntiqlə boolean yoxlanılır
        boolean deleted = clientService.deleteClients(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Müştəri uğurla silindi!");
        } else {
            log.warn("Musteri silinmedi, tapilmadi ID: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Silinmə uğursuz oldu: müştəri tapılmadı.");
        }
        return "redirect:/admin/clients";
    }
}