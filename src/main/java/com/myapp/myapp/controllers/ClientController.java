package com.myapp.myapp.controllers;

import com.myapp.myapp.dtos.ClientDtos.ClientCreateDto;
import com.myapp.myapp.dtos.ClientDtos.ClientDto;
import com.myapp.myapp.dtos.ClientDtos.ClientUpDateDto;
import com.myapp.myapp.services.ClientService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    public String addClient(@Valid @ModelAttribute("clientCreateDto") ClientCreateDto clientCreateDto,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        // @NotBlank/@Email annotasiyaları bunsuz heç vaxt işə düşmürdü -
        // boş adla/email-lə müştəri əlavə etmək mümkün idi.
        if (bindingResult.hasErrors()) {
            log.warn("Müştəri əlavə edilərkən validasiya xətası baş verdi.");
            return "admin/clients/client-add";
        }
        clientService.createClients(clientCreateDto);
        redirectAttributes.addFlashAttribute("successMessage", "Müştəri uğurla əlavə edildi!");
        return "redirect:/admin/clients";
    }

    @GetMapping("/edit/{id}")
    public String editClientForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
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
                               @Valid @ModelAttribute("clientUpDateDto") ClientUpDateDto clientUpDateDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.warn("Müştəri yenilənərkən validasiya xətası baş verdi. ID={}", id);
            return "admin/clients/client-update";
        }
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