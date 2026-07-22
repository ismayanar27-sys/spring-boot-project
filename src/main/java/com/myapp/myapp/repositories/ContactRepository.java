package com.myapp.myapp.repositories;

import com.myapp.myapp.models.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    // Spring Data JPA bu interfeysə görə CRUD (Yaratmaq, Oxumaq, Yeniləmək, Silmək) metodlarını avtomatik yaradır.

    // ƏLAVƏ EDİLDİ: Admin panelində ən yeni mesajları yuxarıda göstərmək üçün.
    List<Contact> findAllByOrderByReceivedAtDesc();
}