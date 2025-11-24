package com.myapp.myapp.repositories;

import com.myapp.myapp.models.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    // Spring Data JPA bu interfeysə görə CRUD (Yaratmaq, Oxumaq, Yeniləmək, Silmək) metodlarını avtomatik yaradır.
}