package com.myapp.myapp.repositories;

import com.myapp.myapp.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    // Ad, email və ya təsvirə görə axtarış metodu.
    // 'Containing' keyword-ü LIKE sorğusu yaradır, axtarış sözünün hər hansı hissədə olmasını yoxlayır.
    List<Client> findByNameContainingOrEmailContainingOrDescriptionContaining(String name, String email, String description);
}