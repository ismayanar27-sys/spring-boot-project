package com.myapp.myapp.services.impl;

import com.myapp.myapp.dtos.ProductDtos.ProductCreateDto;
import com.myapp.myapp.dtos.ProductDtos.ProductDto;
import com.myapp.myapp.dtos.ProductDtos.ProductUpdateDto;
import com.myapp.myapp.models.Product;
import com.myapp.myapp.repositories.ProductRepository;
import com.myapp.myapp.services.CloudinaryService;
import com.myapp.myapp.services.ProductService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final CloudinaryService cloudinaryService;

    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper, CloudinaryService cloudinaryService) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public List<ProductDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(x -> modelMapper.map(x, ProductDto.class)).toList();
    }

    // Məhsul tapılmadıqda 'RuntimeException' atır (Controller üçün)
    @Override
    public ProductDto getProductsId(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Məhsul tapılmadı: ID " + id));
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public boolean createProducts(ProductCreateDto productCreateDto, MultipartFile image) {
        // Məhsul adının unikallığı yoxlanılır
        Product findProduct = productRepository.findProductByName(productCreateDto.getName());
        if (findProduct != null) {
            return false;
        }

        // Şəkilin yüklənib-yüklənməməsi yoxlanılır
        if (image == null || image.isEmpty()) {
            return false;
        }

        try {
            // Şəkil Cloudinary-ə yüklənir
            String photoUrl = cloudinaryService.uploadImage(image);

            // DTO-dan Product obyektinə çevrilir
            Product product = modelMapper.map(productCreateDto, Product.class);
            product.setPhotoUrl(photoUrl); // Şəkil URL-i Product obyektinə əlavə edilir

            productRepository.save(product);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Redaktə DTO-su üçün məhsulu tapır. Əgər tapılmasa, xəta atır (Controller üçün)
    @Override
    public ProductUpdateDto findProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Məhsul tapılmadı: ID " + id));

        return modelMapper.map(product, ProductUpdateDto.class);
    }

    @Override
    @Transactional
    public boolean updateProducts(ProductUpdateDto productUpdateDto, Long id, MultipartFile image) {
        Optional<Product> optionalProduct = productRepository.findById(id);

        // Düzəliş: Məhsul tapılmadıqda false qaytarılır
        if (optionalProduct.isEmpty()) {
            return false;
        }

        Product product = optionalProduct.get();

        // Əgər DTO-dakı ad mövcud məhsulun adı deyilsə, unikallığı yoxla
        if (!product.getName().equalsIgnoreCase(productUpdateDto.getName())) {
            Product existingProduct = productRepository.findProductByName(productUpdateDto.getName());
            if (existingProduct != null) {
                // Ad artıq başqa bir məhsulda istifadə olunur
                return false;
            }
        }

        // Məlumatları DTO-dan mövcud Product-a köçürür
        modelMapper.map(productUpdateDto, product);

        if (image != null && !image.isEmpty()) {
            try {
                // Köhnə şəkli sil (Əvvəlki kodunuzdakı məntiq)
                if (product.getPhotoUrl() != null && !product.getPhotoUrl().isEmpty()) {
                    cloudinaryService.deleteImage(product.getPhotoUrl());
                }

                // Yenisini yüklə
                String photoUrl = cloudinaryService.uploadImage(image);
                product.setPhotoUrl(photoUrl);
            } catch (Exception e) {
                e.printStackTrace();
                // Şəkil yükləmədə problem varsa
                return false;
            }
        }
        // Şəkil yüklənməsə də, DTO-dan gələn digər məlumatlarla məhsulu yaddaşa yaz
        productRepository.save(product);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteProducts(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);

        // Düzəliş: Məhsul tapılmadıqda false qaytarılır
        if (optionalProduct.isEmpty()) {
            return false;
        }

        try {
            Product product = optionalProduct.get();
            // Cloudinary-dən şəkli sil
            cloudinaryService.deleteImage(product.getPhotoUrl());
            productRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<ProductDto> searchProducts(String keyword) {
        String trimmedKeyword = keyword.trim().toLowerCase();
        List<Product> products = productRepository.findByNameContainingOrDescriptionContaining(trimmedKeyword, trimmedKeyword);
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Workers/Products sayğacını dinamikləşdirmək üçün verilənlər bazasındakı
     * bütün məhsulların (Product) sayını qaytarır.
     */
    @Override
    public long countProducts() {
        // ProductRepository interfeysində avtomatik olaraq təmin olunan count() metodunu çağırır.
        return productRepository.count();
    }
}