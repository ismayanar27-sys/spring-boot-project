package com.myapp.myapp.services;

import com.myapp.myapp.dtos.ProductCreateDto;
import com.myapp.myapp.dtos.ProductDto;
import com.myapp.myapp.dtos.ProductUpdateDto;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

// Bu interface məhsullarla bağlı biznes məntiqinin müqaviləsini təyin edir.
// "ProductServiceImpl" sinfi bu interface'i implement edir.
public interface ProductService {
    // Verilənlər bazasından bütün məhsulları gətirir və ProductDto siyahısı şəklində qaytarır.
    List<ProductDto> getAllProducts();
    // Yeni məhsul əlavə edir. Uğurlu olarsa true, əks halda false qaytarır.
    boolean addProduct(ProductCreateDto productCreateDto, MultipartFile image);
    // ID-yə görə məhsulu tapır və ProductUpdateDto obyekti şəklində qaytarır.
    ProductUpdateDto getProductUpdateDtoById(Long id);
    // Mövcud məhsulu yeniləyir.
    boolean updateProduct(ProductUpdateDto productUpdateDto, MultipartFile image);
    // ID-yə görə məhsulu silir.
    boolean deleteProduct(Long id);
}
