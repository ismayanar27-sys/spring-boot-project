package com.myapp.myapp.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Component // Bu annotasiya Springə bu sinfin bir komponent olduğunu və avtomatik idarə olunmalı olduğunu bildirir.
public class FileUploadUtil {

    private final Cloudinary cloudinary;

    public FileUploadUtil(Cloudinary cloudinary) {
        // Cloudinary obyekti "dependency injection" (asılılıq inyeksiyası) vasitəsilə daxil edilir.
        // Bu obyekt CloudinaryConfig sinifində bir bean (komponent) kimi təyin edilib.
        this.cloudinary = cloudinary;
    }

    // Faylı bulud xidmətinə (Cloudinary) yükləyir
    public String uploadFile(MultipartFile multipartFile) {
        try {
            // Cloudinary-ə yükləmə əməliyyatı üçün parametrlər təyin edilir.
            Map uploadResult = cloudinary.uploader().upload(multipartFile.getBytes(), ObjectUtils.emptyMap());
            // Yüklənmiş faylın URL-ini (internet ünvanını) qaytarır.
            return (String) uploadResult.get("url");
        } catch (IOException e) {
            // Yükləmə zamanı hər hansı bir xəta baş verərsə, xəta mesajını çap edir.
            e.printStackTrace();
            return null;
        }
    }
}
