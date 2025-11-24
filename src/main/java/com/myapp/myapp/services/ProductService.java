package com.myapp.myapp.services;

import com.myapp.myapp.dtos.ProductDtos.ProductCreateDto;
import com.myapp.myapp.dtos.ProductDtos.ProductDto;
import com.myapp.myapp.dtos.ProductDtos.ProductUpdateDto;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ProductService {
    // Bütün məhsulları qaytarır
    List<ProductDto> getAllProducts();

    // ID ilə məhsulu qaytarır
    ProductDto getProductsId(Long id);

    // Yeni məhsul yaradır (DTO və şəkil faylını qəbul edir)
    boolean createProducts(ProductCreateDto productCreateDto, MultipartFile image);

    // Mövcud məhsulu yeniləyir (ID, DTO və şəkil faylını qəbul edir)
    boolean updateProducts(ProductUpdateDto productUpdateDto, Long id, MultipartFile image);

    // Məhsulu silir
    boolean deleteProducts(Long id);

    // Yeniləmə forması üçün məhsul məlumatlarını tapır
    ProductUpdateDto findProductById(Long id);

    // Açar sözə əsasən məhsulları axtarır
    List<ProductDto> searchProducts(String keyword);

    // YENİ METOD: Məhsulların ümumi sayını gətirir (Workers sayğacı üçün)
    long countProducts(); // 👈 Bu hissəni əlavə edin!
}