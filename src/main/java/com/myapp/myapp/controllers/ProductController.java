package com.myapp.myapp.controllers;

import com.myapp.myapp.dtos.ProductDtos.ProductCreateDto;
import com.myapp.myapp.dtos.ProductDtos.ProductDto;
import com.myapp.myapp.dtos.ProductDtos.ProductUpdateDto;
import com.myapp.myapp.services.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/products")
@Slf4j
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Məhsullar siyahısı və axtarış nəticələrini göstərir.
    @GetMapping
    public String getAllProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model
    ) {
        List<ProductDto> products;

        if (keyword != null && !keyword.trim().isEmpty()) {
            log.info("Məhsul axtarışı edilir: keyword={}", keyword);

            products = productService.searchProducts(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            products = productService.getAllProducts();
        }

        model.addAttribute("products", products);

        return "admin/products/products";
    }

    // Məhsul detallarını göstərir.
    @GetMapping("/{id}")
    public String getProductById(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            ProductDto product = productService.getProductsId(id);

            model.addAttribute("product", product);

            return "admin/products/product-details";

        } catch (RuntimeException e) {
            log.error("Məhsul tapılmadı. ID={}", id, e);

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Məhsul tapılmadı."
            );

            return "redirect:/admin/products";
        }
    }

    // Yeni məhsul əlavə etmə forması.
    @GetMapping("/add")
    public String addProductForm(Model model) {

        if (!model.containsAttribute("productCreateDto")) {
            model.addAttribute(
                    "productCreateDto",
                    new ProductCreateDto()
            );
        }

        return "admin/products/add-product";
    }

    // Yeni məhsul əlavə edir.
    @PostMapping("/add")
    public String addProduct(
            @Valid @ModelAttribute("productCreateDto")
            ProductCreateDto productCreateDto,

            BindingResult bindingResult,

            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {
            log.warn("Məhsul əlavə edilərkən validation xətası baş verdi.");

            return "admin/products/add-product";
        }

        if (productCreateDto.getImage() == null
                || productCreateDto.getImage().isEmpty()) {

            bindingResult.reject(
                    "image.required",
                    "Şəkil faylı yüklənməlidir."
            );

            return "admin/products/add-product";
        }

        boolean success = productService.createProducts(
                productCreateDto,
                productCreateDto.getImage()
        );

        if (success) {
            log.info(
                    "Məhsul uğurla əlavə edildi. name={}",
                    productCreateDto.getName()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Məhsul uğurla əlavə edildi!"
            );

        } else {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Məhsul adı artıq mövcuddur və ya daxili xəta baş verdi."
            );
        }

        return "redirect:/admin/products";
    }

    // Məhsulu redaktə formasında göstərir.
    @GetMapping("/edit/{id}")
    public String editProductForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {

            if (!model.containsAttribute("productUpdateDto")) {

                ProductUpdateDto productUpdateDto =
                        productService.findProductById(id);

                model.addAttribute(
                        "productUpdateDto",
                        productUpdateDto
                );
            }

            return "admin/products/edit-product";

        } catch (RuntimeException e) {

            log.error(
                    "Redaktə üçün məhsul tapılmadı. ID={}",
                    id,
                    e
            );

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Redaktə etmək istədiyiniz məhsul tapılmadı."
            );

            return "redirect:/admin/products";
        }
    }

    // Məhsulu yeniləyir.
    @PostMapping("/update/{id}")
    public String updateProduct(
            @PathVariable Long id,

            @Valid @ModelAttribute("productUpdateDto")
            ProductUpdateDto productUpdateDto,

            BindingResult bindingResult,

            @RequestParam(
                    value = "image",
                    required = false
            )
            MultipartFile image,

            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {

            log.warn(
                    "Məhsul yenilənərkən validation xətası baş verdi. ID={}",
                    id
            );

            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.productUpdateDto",
                    bindingResult
            );

            redirectAttributes.addFlashAttribute(
                    "productUpdateDto",
                    productUpdateDto
            );

            return "redirect:/admin/products/edit/" + id;
        }

        boolean success = productService.updateProducts(
                productUpdateDto,
                id,
                image
        );

        if (success) {

            log.info(
                    "Məhsul uğurla yeniləndi. ID={}",
                    id
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Məhsul uğurla yeniləndi!"
            );

        } else {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Yenilənmə zamanı xəta baş verdi."
            );
        }

        return "redirect:/admin/products";
    }

    // Məhsulu silir.
    @PostMapping("/delete/{id}")
    public String deleteProduct(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {

        log.info("Məhsul silinir. ID={}", id);

        boolean success = productService.deleteProducts(id);

        if (success) {

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Məhsul uğurla silindi!"
            );

        } else {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Məhsulu silərkən xəta baş verdi. Məhsul tapılmadı."
            );
        }
        return "redirect:/admin/products";
    }
}