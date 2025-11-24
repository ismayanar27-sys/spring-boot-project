package com.myapp.myapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/payments") // DÜZGÜN ÜNVAN
public class PaymentController {

    // Portmanat.az tərəfindən göndərilən geri dönüş sorğusunu idarə edir.
    // URL: /api/payments/callback?status=ok&transactionId=...
    @GetMapping("/callback") // DÜZGÜN ÜNVAN
    public String handlePaymentCallback(@RequestParam("status") String status, @RequestParam("transactionId") String transactionId) {
        if ("ok".equals(status)) {
            return "redirect:/api/orders/checkout/success";
        } else {
            return "redirect:/api/orders/checkout/failure";
        }
    }
}