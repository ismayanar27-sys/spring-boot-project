package com.myapp.myapp.repositstories;

import com.myapp.myapp.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
    // JpaRepository interfeysini miras alaraq, Product entity-si üçün standart CRUD
    // (Create, Read, Update, Delete) əməliyyatlarını avtomatik olaraq əldə edir.
    // Long, entity-nin əsas açarının (primary key) tipidir.

    // Spring Data JPA bu metodun adından asılı olaraq avtomatik olaraq SQL sorğusu yaradır.
    // Bu metod, məhsulu adına görə axtarmaq üçün istifadə olunur.
    Product findProductByName(String name);
}
