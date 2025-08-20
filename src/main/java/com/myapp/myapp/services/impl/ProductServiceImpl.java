package com.myapp.myapp.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.myapp.myapp.dtos.ProductCreateDto;
import com.myapp.myapp.dtos.ProductDto;
import com.myapp.myapp.dtos.ProductUpdateDto;
import com.myapp.myapp.models.Product;
import com.myapp.myapp.repositstories.ProductRepository;
import com.myapp.myapp.services.ProductService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final Cloudinary cloudinary;

    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper, Cloudinary cloudinary) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.cloudinary = cloudinary;
    }

    @Override
    public List<ProductDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(x -> modelMapper.map(x, ProductDto.class)).toList();
    }

    @Override
    public boolean addProduct(ProductCreateDto productCreateDto, MultipartFile image) {
        Product findProduct = productRepository.findProductByName(productCreateDto.getName());
        if (findProduct != null) {
            return false;
        }

        try {
            Map upLoadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
            String photoUrl = (String) upLoadResult.get("url");

            // DTO-dan Entity-yə çevirmə
            Product product = modelMapper.map(productCreateDto, Product.class);
            product.setPhotoUrl(photoUrl);

            productRepository.save(product);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ProductUpdateDto getProductUpdateDtoById(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            return modelMapper.map(product, ProductUpdateDto.class);
        }
        return null;
    }

    // Şəklin Cloudinary-dən silinməsi üçün köməkçi metod.
    private void deleteCloudinaryImage(String photoUrl) {
        if (photoUrl != null && !photoUrl.isEmpty()) {
            try {
                String publicId = photoUrl.substring(photoUrl.lastIndexOf("/") + 1, photoUrl.lastIndexOf("."));
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @Transactional
    public boolean updateProduct(ProductUpdateDto productUpdateDto, MultipartFile image) {
        Optional<Product> optionalProduct = productRepository.findById(productUpdateDto.getId());
        if (optionalProduct.isEmpty()) {
            return false;
        }

        Product product = optionalProduct.get();
        // DTO-dan mövcud Entity-yə məlumatları köçürmə
        modelMapper.map(productUpdateDto, product);

        if (image != null && !image.isEmpty()) {
            try {
                deleteCloudinaryImage(product.getPhotoUrl());

                Map upLoadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                String photoUrl = (String) upLoadResult.get("url");
                product.setPhotoUrl(photoUrl);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        productRepository.save(product);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            try {
                Product product = optionalProduct.get();
                deleteCloudinaryImage(product.getPhotoUrl());

                productRepository.deleteById(id);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}