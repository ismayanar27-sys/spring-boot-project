package com.myapp.myapp.controllers;

import com.myapp.myapp.dtos.ProductCreateDto;
import com.myapp.myapp.dtos.ProductUpdateDto;
import com.myapp.myapp.services.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String getAllProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "admin/products/products";
    }

    @GetMapping("/add")
    public String addProductForm(Model model) {
        model.addAttribute("productCreateDto", new ProductCreateDto());
        return "admin/add-product"; // Düzgün yol
    }

    @PostMapping("/add")
    public String addProduct(@ModelAttribute ProductCreateDto productCreateDto,
                             @RequestParam("image") MultipartFile image,
                             RedirectAttributes redirectAttributes) {
        productService.addProduct(productCreateDto, image);
        redirectAttributes.addFlashAttribute("successMessage", "Məhsul uğurla əlavə edildi!");
        return "redirect:/admin/products";
    }

    @GetMapping("/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        model.addAttribute("productUpdateDto", productService.getProductUpdateDtoById(id));
        return "admin/edit-product"; // Düzgün yol
    }

    @PostMapping("/update")
    public String updateProduct(@ModelAttribute ProductUpdateDto productUpdateDto,
                                @RequestParam(value = "image", required = false) MultipartFile image,
                                RedirectAttributes redirectAttributes) {
        productService.updateProduct(productUpdateDto, image);
        redirectAttributes.addFlashAttribute("successMessage", "Məhsul uğurla yeniləndi!");
        return "redirect:/admin/products";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("successMessage", "Məhsul uğurla silindi!");
        return "redirect:/admin/products";
    }
}