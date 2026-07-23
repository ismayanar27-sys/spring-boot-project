package com.myapp.myapp.controllers;

import com.myapp.myapp.services.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@Controller
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final OrderService orderService;

    // Render-də hələ təyin etməmisən - o vaxta qədər boş qalır, kod bunu özü idarə edir.
    @Value("${portmanat.secret-key:}")
    private String secretKey;

    // Portmanat.az tərəfindən göndərilən geri dönüş sorğusunu idarə edir.
    // URL: /api/payments/callback?status=ok&transactionId=...&sign=...
    //"sign" parametri hələ göndərilmir (kart inteqrasiyası hazır deyil) -
    // ona görə "required = false" qoyulub, kart olmadan da 400 xətası vermir.
    @GetMapping("/callback")
    public String handlePaymentCallback(
            @RequestParam("status") String status,
            @RequestParam("transactionId") String transactionId,
            @RequestParam(value = "sign", required = false) String sign
    ) {
        if (!isSignatureValid(status, transactionId, sign)) {
            log.error("Ödəniş callback-də imza doğrulanmadı! transactionId={}", transactionId);
            return "redirect:/api/orders/checkout/failure?transactionId=" + transactionId;
        }

        boolean success = "ok".equalsIgnoreCase(status);

        try {
            orderService.confirmPaymentByTransactionId(transactionId, success);
        } catch (EntityNotFoundException e) {
            log.error("Callback-də bilinməyən transactionId: {}", transactionId);
            return "redirect:/api/orders/checkout/failure?transactionId=" + transactionId;
        }

        if (success) {
            return "redirect:/api/orders/checkout/success?transactionId=" + transactionId;
        } else {
            return "redirect:/api/orders/checkout/failure?transactionId=" + transactionId;
        }
    }

    /**
     * HMAC-SHA256 ilə imza yoxlaması.
     *
     * KART HAZIR OLMAYANDA: secretKey boşdur -> yoxlama avtomatik keçir, log-a
     * xəbərdarlıq yazılır, sistem normal işləməyə davam edir.
     *
     * KART HAZIR OLANDA İŞ:
     * 1) Render-də PORTMANAT_SECRET_KEY environment variable-ını təyin eDILIR.
     * 2) Portmanat-ın rəsmi sənədlərinə bax - onlar HANSI sahələri (status,
     *    transactionId, amount və s.), HANSI SIRAYLA və HANSI hash alqoritmi
     *    ilə imzalayır. Aşağıdakı "payload" sətrini elə buna uyğun dəyiş.
     *    (Hazırkı "status:transactionId" formatı YALNIZ nümunədir, real
     *    Portmanat formatı ilə üst-üstə düşməyə bilər.)
     */
    private boolean isSignatureValid(String status, String transactionId, String sign) {
        if (secretKey == null || secretKey.isBlank()) {
            log.warn("PORTMANAT_SECRET_KEY təyin olunmayıb - imza yoxlaması ATLANILIR (yalnız kart hazır olana qədər normaldır).");
            return true;
        }

        if (sign == null || sign.isBlank()) {
            return false;
        }

        try {
            String payload = status + ":" + transactionId;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String expectedSign = HexFormat.of().formatHex(hash);
            return expectedSign.equalsIgnoreCase(sign);
        } catch (Exception e) {
            log.error("İmza hesablanarkən xəta baş verdi", e);
            return false;
        }
    }
}