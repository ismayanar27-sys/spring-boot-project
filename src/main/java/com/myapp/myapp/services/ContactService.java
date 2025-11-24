package com.myapp.myapp.services;

import com.myapp.myapp.dtos.ContactDTO;
import com.myapp.myapp.models.Contact; // DÜZƏLİŞ: models paketi (entities əvəzinə)

// ...

public interface ContactService {

    // Artıq tək bir metod var
    Contact saveAndSendContactMessage(ContactDTO contactDTO);
}