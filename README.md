# Restoran İdarəetmə Sistemi (Full-Stack Spring Boot Layihəsi)

Restoranlar üçün hazırlanmış tam funksional veb tətbiq — məhsul kataloqu, onlayn sifariş/ödəniş, masa rezervasiyası, əlaqə formu və admin idarəetmə paneli daxil olmaqla.

🔗 **Canlı Demo:** [spring-boot-project-em4j.onrender.com](https://spring-boot-project-em4j.onrender.com)

---

##  Xüsusiyyətlər

- **Məhsul kataloqu** — kateqoriyalara bölünmüş menyu, Cloudinary üzərindən şəkil yükləmə
- **Onlayn sifariş & səbət** — sessiya-əsaslı səbət idarəetməsi, ödəniş inteqrasiyası
- **Masa rezervasiyası** — tarix/saat seçimi ilə onlayn rezervasiya, admin bildirişi
- **Əlaqə formu** — müştəri müraciətləri, avtomatik e-poçt bildirişi
- **Admin paneli** — Spring Security ilə qorunan, məhsul/sifariş idarəetməsi
- **E-poçt bildirişləri** — Gmail SMTP inteqrasiyası ilə HTML formatlı mail göndərmə

##  Texnologiyalar

| Kateqoriya | Texnologiya |
|---|---|
| Backend | Java 17, Spring Boot 3.5.4 |
| Verilənlər bazası | PostgreSQL, Spring Data JPA / Hibernate |
| Təhlükəsizlik | Spring Security, BCrypt |
| Frontend | Thymeleaf, HTML5, CSS3, JavaScript |
| Şəkil saxlama | Cloudinary |
| E-poçt | Spring Mail (Gmail SMTP) |
| Konteynerləşdirmə | Docker |
| Deployment | Render (backend), Neon (PostgreSQL) |
| Build aləti | Maven |

## 📂 Layihə Strukturu

```
src/main/java/com/myapp/myapp/
├── configs/         # Security, Cloudinary, ModelMapper konfiqurasiyaları
├── controllers/      # REST & MVC endpoint-lər
├── dtos/             # Data Transfer Object-lər
├── models/           # JPA Entity-lər
├── repositories/      # Spring Data JPA repository-lər
└── services/          # Biznes məntiqi (interfeys + implementasiya)
```

## ⚙️ Lokal Quraşdırma

1. Repo-nu klonla:
   ```bash
   git clone https://github.com/ismayanar27-sys/spring-boot-project.git
   ```
2. PostgreSQL-də yeni baza yarat
3. Aşağıdakı environment variable-ları təyin et:
   ```
   DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD
   CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, CLOUDINARY_API_SECRET
   MAIL_USERNAME, MAIL_PASSWORD, ADMIN_EMAIL
   APP_ADMIN_USERNAME, APP_ADMIN_PASSWORD
   ```
4. Tətbiqi işə sal:
   ```bash
   ./mvnw spring-boot:run
   ```

## 👤 Müəllif

**Anar İsmayılov** — Full Stack Developer (Java / Spring Boot)
