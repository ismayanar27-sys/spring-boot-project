package com.myapp.myapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    /**
     * Spring Security /admin/** URL-ə müraciət edən istifadəçini /login URL-ə yönləndirir.
     * Bu metod /login requestini tutur və login səhifəsinin şablonunu göstərir.
     * @return Thymeleaf şablonunun yolu ("front/login")
     */
    @GetMapping("/login")
    public String login() {
        // Bu, templates/front/login.html faylını göstərməyi təmin edir.
        return "front/login";
    }
}
