package com.myapp.myapp.configs;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // Spring Framework'ə bu sinfin konfiqurasiya sinfi olduğunu bildirir.
public class Configures {
    @Bean // Spring konteynerində "ModelMapper" tipli bir bean (obyekt) yaradır və onu idarə edir.
    public ModelMapper modelMapper() {
        // Yeni bir ModelMapper obyekti yaradır və onu qaytarır.
        return new ModelMapper();
    }
}
