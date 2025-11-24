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
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            // Yüklənmiş şəklin URL-ini qaytarmaq
            return uploadResult.get("url").toString();
        } catch (IOException e) {
            // Yükləmə zamanı xəta baş verərsə, xətanı istisna kimi atmaq
            throw new RuntimeException("Şəkil yüklənərkən xəta baş verdi.", e);
        }
    }

    // Bu metodu əlavə etməlisiniz!
    public void deleteImage(String photoUrl) {
        if (photoUrl != null && !photoUrl.isEmpty()) {
            try {
                // Public ID-ni URL-dən çıxarırıq (son slash-dan sonra və . uzantısından əvvəlki hissə)
                String publicId = photoUrl.substring(photoUrl.lastIndexOf("/") + 1, photoUrl.lastIndexOf("."));
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            } catch (IOException e) {
                // Şəkil silinərkən xəta baş verərsə, onu qeyd edirik
                e.printStackTrace();
            }
        }
    }
}