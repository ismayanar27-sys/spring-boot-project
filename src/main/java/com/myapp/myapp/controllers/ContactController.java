package com.myapp.myapp.controllers;

import com.myapp.myapp.dtos.ContactDTO;
import com.myapp.myapp.services.ContactService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.http.MediaType;

@Controller
public class ContactController {

    private final ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    // Sadəcə /contact ünvanına GET müraciəti edildikdə contact.html-i göstərir
    @GetMapping("/contact")
    public String showContactPage() {
        return "front/contact";
    }

    // Əlaqə Formunun AJAX müraciətini idarə edir
    // DÜZƏLİŞ: Form Data (x-www-form-urlencoded) qəbul edirik və @ModelAttribute istifadə edirik
    @PostMapping(value = "/contact", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public String handleContactSubmission(
            @Valid @ModelAttribute ContactDTO contactDTO, // @RequestBody yerinə @ModelAttribute!
            BindingResult bindingResult
    ) {
        // 1. Validasiya xətalarını yoxla
        if (bindingResult.hasErrors()) {
            // Frontend-in başa düşəcəyi ERROR cavabını qaytarır
            return "ERROR: Zəhmət olmasa formanı düzgün doldurun.";
        }

        try {
            // 2. Məlumatı DB-yə yazır və e-mail göndərir
            contactService.saveAndSendContactMessage(contactDTO);

            // 3. Uğurlu cavab
            return "SUCCESS: Mesajınız uğurla göndərildi! Təşəkkür edirik.";

        } catch (Exception e) {
            System.err.println("❌ Kontakt form emalı zamanı xəta: " + e.getMessage());
            e.printStackTrace();
            return "ERROR: Serverdə gözlənilməyən xəta baş verdi. Zəhmət olmasa, yenidən cəhd edin.";
        }
    }
}