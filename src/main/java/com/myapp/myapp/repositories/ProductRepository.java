package com.myapp.myapp.repositories;

import com.myapp.myapp.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    // JpaRepository interfeysini miras alaraq, Product entity-si üçün standart CRUD
    // (Create, Read, Update, Delete) əməliyyatlarını avtomatik olaraq əldə edir.
    // Long, entity-nin əsas açarının (primary key) tipidir.

    // Spring Data JPA bu metodun adından asılı olaraq avtomatik olaraq SQL sorğusu yaradır.
    // Bu metod, məhsulu adına görə axtarmaq üçün istifadə olunur.
    Product findProductByName(String name);

    // Yeni axtarış metodu əlavə edildi.
    // Ad və ya təsvirdə axtarış sözünü axtarmaq üçün istifadə olunur.
    // 'Containing' keyword-ü "LIKE" sorğusu yaradır.
    List<Product> findByNameContainingOrDescriptionContaining(String name, String description);
}