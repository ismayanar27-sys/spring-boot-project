package com.myapp.myapp.controllers;

import com.myapp.myapp.dtos.ProductDtos.ProductCreateDto;
import com.myapp.myapp.dtos.ProductDtos.ProductDto;
import com.myapp.myapp.dtos.ProductDtos.ProductUpdateDto;
import com.myapp.myapp.services.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j; // ELAVE EDILDI: Loglama ucun
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/products")
@Slf4j // ELAVE EDILDI: Audit ucun loglama aktiv edildi
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Əsas məhsullar siyahısı və axtarış nəticələrini göstərmək üçün metod
    @GetMapping
    public String getAllProducts(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<ProductDto> products;
        if (keyword != null && !keyword.isEmpty()) {
            log.info("Mehsul axtarisi edilir: keyword = {}", keyword); // AUDIT LOG
            products = productService.searchProducts(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            products = productService.getAllProducts();
        }
        model.addAttribute("products", products);
        return "admin/products/products";
    }

    @GetMapping("/{id}")
    public String getProductById(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            ProductDto product = productService.getProductsId(id);
            model.addAttribute("product", product);
            return "admin/products/product-details";
        } catch (RuntimeException e) {
            log.error("Mehsul tapilmadi ID: {}", id); // AUDIT LOG
            redirectAttributes.addFlashAttribute("errorMessage", "Xəta: Məhsul ID-si (" + id + ") tapılmadı.");
            return "redirect:/admin/products";
        }
    }

    // GET /add: Yeni məhsul əlavə etmə forması
    @GetMapping("/add")
    public String addProductForm(Model model) {
        if (!model.containsAttribute("productCreateDto")) {
            model.addAttribute("productCreateDto", new ProductCreateDto());
        }
        return "admin/products/add-product";
    }

    // POST /add: Yeni məhsul əlavə etmə prosesi
    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute("productCreateDto") ProductCreateDto productCreateDto,
                             BindingResult bindingResult,
                             @RequestParam("image") MultipartFile image,
                             RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            log.warn("Yeni mehsul elave edilerken validasiya xetasi bas verdi"); // AUDIT LOG
            return "admin/products/add-product";
        }

        if (image.isEmpty()) {
            bindingResult.rejectValue("name", "error.productCreateDto", "Şəkil faylı yüklənməlidir.");
            return "admin/products/add-product";
        }

        boolean success = productService.createProducts(productCreateDto, image);

        if (success) {
            log.info("Mehsul ugurla elave edildi: {}", productCreateDto.getName()); // AUDIT LOG
            redirectAttributes.addFlashAttribute("successMessage", "Məhsul uğurla əlavə edildi!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Məhsul adı artıq mövcuddur və ya daxili xəta baş verdi.");
        }

        return "redirect:/admin/products";
    }

    // GET /edit/{id}: Məhsulu redaktə formasına göndərən metod
    @GetMapping("/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Əgər validasiya xətası yoxdursa (yəni birbaşa GEt ilə gəlirsə)
            if (!model.containsAttribute("productUpdateDto")) {
                ProductUpdateDto productUpdateDto = productService.findProductById(id);
                model.addAttribute("productUpdateDto", productUpdateDto);
            }
            return "admin/products/edit-product";
        } catch (RuntimeException e) {
            log.error("Redakte ucun mehsul tapilmadi ID: {}", id); // AUDIT LOG
            // Məhsul tapılmadıqda
            redirectAttributes.addFlashAttribute("errorMessage", "Redaktə etmək istədiyiniz məhsul tapılmadı.");
            return "redirect:/admin/products";
        }
    }

    // POST /update/{id}: Yeniləmə prosesi
    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute("productUpdateDto") ProductUpdateDto productUpdateDto,
                                BindingResult bindingResult,
                                @RequestParam(value = "image", required = false) MultipartFile image,
                                RedirectAttributes redirectAttributes) {

        // 1. DTO validasiya səhvlərini yoxla
        if (bindingResult.hasErrors()) {
            log.warn("Mehsul yenilenirken validasiya xetasi (ID: {})", id); // AUDIT LOG
            // RedirectAttributes vasitəsilə BindingResult-u və DTO-nu ötürürük.
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.productUpdateDto", bindingResult);
            redirectAttributes.addFlashAttribute("productUpdateDto", productUpdateDto);
            // Və GET metoduna yönləndir
            return "redirect:/admin/products/edit/" + id;
        }

        // 2. Service-ə ötür
        boolean success = productService.updateProducts(productUpdateDto, id, image);

        if (success) {
            log.info("Mehsul yenilendi ID: {}", id); // AUDIT LOG
            redirectAttributes.addFlashAttribute("successMessage", "Məhsul uğurla yeniləndi!");
        } else {
            // Məhsul tapılmadıqda və ya ad təkrarlandıqda
            redirectAttributes.addFlashAttribute("errorMessage", "Yenilənmə zamanı xəta: Məhsul tapılmadı və ya ad artıq mövcuddur.");
        }

        return "redirect:/admin/products";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        log.info("Mehsul silinir ID: {}", id); // AUDIT LOG
        boolean success = productService.deleteProducts(id);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Məhsul uğurla silindi!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Məhsulu silərkən xəta baş verdi. Məhsul tapılmadı.");
        }
        return "redirect:/admin/products";
    }
}