package com.myapp.myapp.services.impl;

import com.myapp.myapp.dtos.ProductDtos.ProductCreateDto;
import com.myapp.myapp.dtos.ProductDtos.ProductDto;
import com.myapp.myapp.dtos.ProductDtos.ProductUpdateDto;
import com.myapp.myapp.models.Product;
import com.myapp.myapp.repositories.ProductRepository;
import com.myapp.myapp.services.CloudinaryService;
import com.myapp.myapp.services.ProductService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final CloudinaryService cloudinaryService;

    public ProductServiceImpl(
            ProductRepository productRepository,
            ModelMapper modelMapper,
            CloudinaryService cloudinaryService
    ) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    @Cacheable(value = "products")
    public List<ProductDto> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();
    }

    @Override
    public ProductDto getProductsId(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Məhsul tapılmadı: ID " + id)
                );

        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public boolean createProducts(
            ProductCreateDto productCreateDto,
            MultipartFile image
    ) {

        Product existingProduct =
                productRepository.findByNameIgnoreCase(productCreateDto.getName());

        if (existingProduct != null) {
            return false;
        }

        if (image == null || image.isEmpty()) {
            return false;
        }

        try {

            String photoUrl = cloudinaryService.uploadImage(image);

            Product product =
                    modelMapper.map(productCreateDto, Product.class);

            product.setPhotoUrl(photoUrl);

            productRepository.save(product);

            log.info(
                    "Məhsul uğurla yaradıldı. name={}",
                    product.getName()
            );

            return true;

        } catch (Exception e) {

            log.error(
                    "Məhsul yaradılarkən xəta baş verdi. name={}",
                    productCreateDto.getName(),
                    e
            );

            return false;
        }
    }

    @Override
    public ProductUpdateDto findProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Məhsul tapılmadı: ID " + id)
                );

        return modelMapper.map(product, ProductUpdateDto.class);
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public boolean updateProducts(
            ProductUpdateDto productUpdateDto,
            Long id,
            MultipartFile image
    ) {

        Product product = productRepository.findById(id)
                .orElse(null);

        if (product == null) {
            return false;
        }

        if (!product.getName().equalsIgnoreCase(productUpdateDto.getName())) {

            Product existingProduct =
                    productRepository.findByNameIgnoreCase(
                            productUpdateDto.getName()
                    );

            if (existingProduct != null) {
                return false;
            }
        }

        String oldPhotoUrl = product.getPhotoUrl();
        String newPhotoUrl = null;

        try {

            /*
             * YENİ ŞƏKİL ƏVVƏL CLOUDINARY-YƏ YÜKLƏNİR.
             * Köhnə şəkil hələ silinmir.
             */
            if (image != null && !image.isEmpty()) {

                newPhotoUrl = cloudinaryService.uploadImage(image);
            }

            /*DTO-dakı məhsul məlumatları mövcud Entity-yə köçürülür.
             * ProductUpdateDto-dan id silinməlidir.
             * Entity ID-si URL-dəki id ilə idarə olunur.
             */
            modelMapper.map(productUpdateDto, product);

            if (newPhotoUrl != null) {
                product.setPhotoUrl(newPhotoUrl);
            }

            productRepository.save(product);

            /*
             * Database save-dən sonra köhnə şəkil silinir.
             */
            if (newPhotoUrl != null
                    && oldPhotoUrl != null
                    && !oldPhotoUrl.isBlank()) {

                cloudinaryService.deleteImage(oldPhotoUrl);
            }

            log.info(
                    "Məhsul uğurla yeniləndi. ID={}",
                    id
            );

            return true;

        } catch (Exception e) {

            /*
             * Yeni şəkil yüklənib, amma sonrakı mərhələdə xəta baş veribsə,
             * yeni Cloudinary şəklini silməyə çalışırıq.
             */
            if (newPhotoUrl != null) {

                try {
                    cloudinaryService.deleteImage(newPhotoUrl);
                } catch (Exception cleanupException) {

                    log.error(
                            "Yeni Cloudinary şəklinin cleanup prosesi uğursuz oldu. ID={}",
                            id,
                            cleanupException
                    );
                }
            }

            log.error(
                    "Məhsul yenilənərkən xəta baş verdi. ID={}",
                    id,
                    e
            );

            return false;
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public boolean deleteProducts(Long id) {

        Product product = productRepository.findById(id)
                .orElse(null);

        if (product == null) {
            return false;
        }

        try {

            String photoUrl = product.getPhotoUrl();

            productRepository.delete(product);

            if (photoUrl != null && !photoUrl.isBlank()) {
                cloudinaryService.deleteImage(photoUrl);
            }

            log.info(
                    "Məhsul uğurla silindi. ID={}",
                    id
            );

            return true;

        } catch (Exception e) {

            log.error(
                    "Məhsul silinərkən xəta baş verdi. ID={}",
                    id,
                    e
            );

            return false;
        }
    }

    @Override
    public List<ProductDto> searchProducts(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return getAllProducts();
        }

        String trimmedKeyword = keyword.trim();

        return productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        trimmedKeyword,
                        trimmedKeyword
                )
                .stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();
    }

    @Override
    public long countProducts() {
        return productRepository.count();
    }
}
