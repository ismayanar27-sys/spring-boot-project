package com.myapp.myapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Spring'ə bu sinfin bir web kontroller olduğunu bildirir.
public class IndexController {

    @GetMapping("/") // Əsas URL-inə ("/") gələn GET sorğularını emal edir.
    public String home(){
        return "index"; // "templates/index.html" səhifəsini göstərir.
    }
}
