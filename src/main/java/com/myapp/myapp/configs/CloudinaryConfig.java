package com.myapp.myapp.configs;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // Spring Framework'ə bu sinfin konfiqurasiya sinfi olduğunu bildirir.
public class CloudinaryConfig {
    // application.properties faylında qeyd olunan Cloudinary hesabının adını bu dəyişənə verir.
    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    // Cloudinary API açarını application.properties-dən alır.
    @Value("${cloudinary.api-key}")
    private String apiKey;

    // Cloudinary API gizli açarını application.properties-dən alır.
    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Bean // Spring konteynerində bu metodu icra edərək bir "Cloudinary" obyekti yaradır və onu idarə edir.
    public Cloudinary cloudinary() {
        // Cloudinary obyektini yuxarıdakı dəyişənlərlə konfiqurasiya edir və qaytarır.
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }
}