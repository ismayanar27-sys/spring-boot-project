package com.myapp.myapp.controllers;

import com.myapp.myapp.models.Contact;
import com.myapp.myapp.services.ContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Admin panelində "Contact Messages" bölməsini idarə edir.
 * İstifadəçilərin /contact formundan göndərdiyi mesajları göstərir və silməyə imkan verir.
 */
@Controller
@RequestMapping("/admin/contacts")
@RequiredArgsConstructor
@Slf4j
public class AdminContactController {

    private final ContactService contactService;

    @GetMapping
    public String getAllMessages(Model model) {

        log.info("Bütün əlaqə mesajları gətirilir.");

        List<Contact> messages = contactService.getAllMessages();

        model.addAttribute("messages", messages);

        return "admin/contacts/contacts-list";
    }

    @PostMapping("/delete/{id}")
    public String deleteMessage(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        boolean deleted = contactService.deleteMessage(id);

        if (deleted) {
            log.info("Əlaqə mesajı silindi. ID={}", id);
            redirectAttributes.addFlashAttribute("successMessage", "Mesaj uğurla silindi.");
        } else {
            log.warn("Silinəcək mesaj tapılmadı. ID={}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Mesaj tapılmadı.");
        }

        return "redirect:/admin/contacts";
    }
}