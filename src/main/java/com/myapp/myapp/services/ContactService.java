package com.myapp.myapp.services;

import com.myapp.myapp.dtos.ContactDTO;
import com.myapp.myapp.models.Contact; // DÜZƏLİŞ: models paketi (entities əvəzinə)

import java.util.List;

public interface ContactService {

    // Artıq tək bir metod var
    Contact saveAndSendContactMessage(ContactDTO contactDTO);

    // ƏLAVƏ EDİLDİ: admin panelində mesajları görmək/idarə etmək üçün.
    List<Contact> getAllMessages();

    boolean deleteMessage(Long id);

    long countMessages();
}