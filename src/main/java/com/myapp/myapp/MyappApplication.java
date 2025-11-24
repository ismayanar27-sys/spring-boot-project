package com.myapp.myapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan; // ComponentScan import edildi

@SpringBootApplication
@ComponentScan(basePackages = "com.myapp.myapp") // Bütün alt paketləri məcburi skan etmək üçün əlavə edildi
public class MyappApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyappApplication.class, args);
	}
}