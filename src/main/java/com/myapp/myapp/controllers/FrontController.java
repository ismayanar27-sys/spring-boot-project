package com.myapp.myapp.controllers;

import com.myapp.myapp.dtos.ProductDtos.ProductDto;
import com.myapp.myapp.services.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class FrontController {

    private final ProductService productService;

    public FrontController(ProductService productService) {
        this.productService = productService;
    }

    // 1. Məhsullar Səhifəsi (/products)
    @GetMapping("/products")
    public String showUserProducts(Model model) {

        List<ProductDto> products = productService.getAllProducts();
        model.addAttribute("products", products);

        // Şablon yolu: templates/front/menu.html
        return "front/menu";
    }

    // 2. Haqqımızda Səhifəsi (/about)
    @GetMapping("/about")
    public String showAboutPage() {
        // Şablon yolu: templates/front/about.html
        return "front/about";
    }

    // 3. Aşpazlar Səhifəsi (/chefs)
    @GetMapping("/chefs")
    public String showChefsPage() {
        // Şablon yolu: templates/front/chefs.html
        return "front/chefs";
    }

    // Qeyd: /contact mapinqi (FrontController-da olan) məsuliyyətinə görə ContactController-a köçürülmüşdür.
    // Bu, hər bir Kontrollerin yalnız bir işlə məşğul olmasını təmin edir.
}