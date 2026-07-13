package com.myapp.myapp.controllers;

import com.myapp.myapp.dtos.ContactDTO;
import com.myapp.myapp.services.ContactService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j; // ELAVE EDILDI: Loglama ucun
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.http.MediaType;

@Controller
@Slf4j // ELAVE EDILDI: Audit ucun loglama aktiv edildi
public class ContactController {

    private final ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/contact")
    public String showContactPage() {
        return "front/contact";
    }

    @PostMapping(value = "/contact", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public String handleContactSubmission(
            @Valid @ModelAttribute ContactDTO contactDTO,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            log.warn("Kontakt formunda validasiya xetasi bas verdi");
            return "ERROR: Zəhmət olmasa formanı düzgün doldurun.";
        }

        try {
            contactService.saveAndSendContactMessage(contactDTO);
            log.info("Kontakt mesaji ugurla qeyde alindi: {}", contactDTO.getEmail());
            return "SUCCESS: Mesajınız uğurla göndərildi! Təşəkkür edirik.";

        } catch (Exception e) {
            log.error("Kontakt form emali zamani xeta bas verdi", e);
            return "ERROR: Serverdə gözlənilməyən xəta baş verdi. Zəhmət olmasa, yenidən cəhd edin.";
        }
    }
}