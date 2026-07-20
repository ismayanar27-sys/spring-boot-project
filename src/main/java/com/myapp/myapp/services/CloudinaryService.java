package com.myapp.myapp.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) {
        try {
            // Şəkli Cloudinary-ə yükləmək və nəticəni Map olaraq almaq
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            //Yüklənmiş şəklin URL-ini qaytarmaq
            //secure_url istifadə olunur ki, HTTPS URL qaytarılsın.
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            // Yükləmə zamanı xəta baş verərsə, xətanı istisna kimi atmaq
            throw new RuntimeException("Şəkil yüklənərkən xəta baş verdi.", e);
        }
    }

    public void deleteImage(String photoUrl) {
        if (photoUrl != null && !photoUrl.isBlank()) {
            try {
                String publicId = extractPublicId(photoUrl);

                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            } catch (IOException e) {
                // Şəkil silinərkən xəta baş verərsə, onu istisna kimi yuxarı ötürürük.
                throw new RuntimeException("Şəkil silinərkən xəta baş verdi.", e);
            }
        }
    }

    //Cloudinary URL-dən public ID-ni düzgün çıxarmaq üçün istifadə olunur.
    private String extractPublicId(String photoUrl) {

        String publicId = photoUrl.substring(
                photoUrl.indexOf("/upload/") + 8
        );

        publicId = publicId.substring(
                publicId.indexOf("/") + 1
        );

        int extensionIndex = publicId.lastIndexOf(".");

        if (extensionIndex != -1) {
            publicId = publicId.substring(0, extensionIndex);
        }

        return publicId;
    }
}
