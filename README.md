# 🍽️ Restoran İdarəetmə Sistemi

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Neon-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

**Full-stack Spring Boot** restoran idarəetmə platforması — müştəri tərəfi (menyu, onlayn sifariş, ödəniş, masa rezervasiyası) ilə tam funksional, təhlükəsizliyi təmin edilmiş admin idarəetmə panelini bir yerdə birləşdirir.

🔗 **Canlı Demo:** [spring-boot-project-em4j.onrender.com](https://spring-boot-project-em4j.onrender.com)

---

## 📌 Layihə haqqında

Bu layihə real bir restoran biznesi üçün hazırlanmış, istehsalata (production) yönləndirilmiş tam funksional bir tətbiqdir. Müştəri tərəfi (məhsul kataloqu, onlayn sifariş, ödəniş, masa rezervasiyası, əlaqə) admin idarəetmə paneli ilə tam inteqrasiya olunub və Spring Security ilə qorunur.

Layihə DTO/Entity ayrımı, servis-yönümlü arxitektura, verilənlər bazası səviyyəsində optimallaşdırılmış sorğular və CSRF-qorunmuş formalarla təhlükəsizlik yönümlü inkişaf prinsiplərinə əsaslanır.

## ✨ Xüsusiyyətlər

### 🧑‍🍳 Müştəri tərəfi
- **Məhsul kataloqu** — Cloudinary üzərindən şəkil saxlama, kateqoriya/açar sözə görə case-insensitive axtarış
- **Onlayn sifariş & səbət** — sessiya-əsaslı səbət idarəetməsi
- **Ödəniş inteqrasiyası** — Portmanat ödəniş sistemi ilə HMAC-SHA256 imza doğrulaması, `transactionId` əsaslı callback emalı, idempotent status yeniləmə (PAID/FAILED)
- **Masa rezervasiyası** — tarix/saat seçimi, restoran tutumuna (capacity) görə real-vaxt yoxlama, admin bildirişi
- **Əlaqə formu** — müştəri müraciətləri, avtomatik e-poçt bildirişi
- **Dinamik statistika sayğacları** — ana səhifədə real bazadan gələn müştəri/rezervasiya/məhsul sayları

### 🔐 Admin paneli (`/admin/**`, Spring Security ilə qorunur)

| Modul | Ünvan | Funksionallıq |
|---|---|---|
| Dashboard | `/admin` | Ümumi say göstəriciləri, modullara sürətli keçid |
| Məhsullar | `/admin/products` | CRUD, şəkil yükləmə, axtarış |
| Müştərilər (CRM) | `/admin/clients` | CRUD, validasiyalı forma, axtarış |
| Sifarişlər | `/admin/orders` | Status izləmə və yeniləmə |
| Rezervasiyalar | `/admin/reservations` | Status üzrə filtrasiya və yeniləmə |
| Əlaqə mesajları | `/admin/contacts` | Daxil olan müraciətlərin siyahısı və silinməsi |
| Statistika | `/admin/statistics` | Sifariş/rezervasiya statusu üzrə bölgü, ümumi gəlir |

## 🛠️ Texnologiyalar

| Kateqoriya | Texnologiya |
|---|---|
| Backend | Java 17, Spring Boot 3.5.4 |
| Verilənlər bazası | PostgreSQL (Neon), Spring Data JPA / Hibernate |
| Təhlükəsizlik | Spring Security (CSRF, BCrypt, rol-əsaslı icazələr) |
| Frontend | Thymeleaf, HTML5, CSS3, Bootstrap 5, JavaScript |
| Şəkil saxlama | Cloudinary |
| E-poçt | Brevo (HTTP API) |
| Ödəniş | Portmanat (HMAC-SHA256 imza doğrulaması) |
| Konteynerləşdirmə | Docker (çox mərhələli build) |
| Deployment | Render (backend), Neon (PostgreSQL) |
| Build aləti | Maven |

> **Qeyd:** E-poçt xidməti əvvəlcə Gmail SMTP ilə işləyirdi, lakin Render-in pulsuz planı SMTP portlarına (587) çıxışı blokladığı üçün Brevo-nun HTTP API-sinə keçirilib.

## 📂 Layihə strukturu

```
src/main/java/com/myapp/myapp/
├── configs/          # Security, Cloudinary, ModelMapper konfiqurasiyaları
├── controllers/       # REST & MVC endpoint-lər (front + admin)
├── dtos/              # Data Transfer Object-lər (validasiya annotasiyaları ilə)
├── models/            # JPA Entity-lər və enum-lar
├── repositories/       # Spring Data JPA repository-ləri
└── services/           # Biznes məntiqi (interfeys + implementasiya)

src/main/resources/
├── templates/
│   ├── front/          # Müştəri tərəfi səhifələri (+ fragments/header, fragments/footer)
│   └── admin/           # Admin paneli (products, clients, orders, reservations, contacts, statistics)
└── static/assets/       # CSS, JS, şəkillər
```

## 🔒 Təhlükəsizlik

- Bütün admin `POST`/`PUT`/`DELETE` sorğuları CSRF token ilə qorunur.
- Parollar BCrypt ilə hash-lənir, admin istifadəçisi verilənlər bazasında saxlanılır.
- Rol-əsaslı icazələr (`/admin/**` yalnız `ROLE_ADMIN`) Spring Security vasitəsilə tətbiq olunur.
- Ödəniş callback-ləri HMAC-SHA256 imza doğrulaması ilə qorunur.

## ⚙️ Lokal quraşdırma

1. Repo-nu klonla:
```bash
   git clone https://github.com/ismayanar27-sys/spring-boot-project.git
   cd spring-boot-project
```

2. PostgreSQL-də yeni baza yarat.

3. Aşağıdakı environment variable-ları təyin et:

```bash
   # Verilənlər bazası
   DATABASE_URL=jdbc:postgresql://...
   DATABASE_USERNAME=...
   DATABASE_PASSWORD=...

   # Cloudinary (şəkil saxlama)
   CLOUDINARY_CLOUD_NAME=...
   CLOUDINARY_API_KEY=...
   CLOUDINARY_API_SECRET=...

   # Brevo (e-poçt bildirişləri)
   BREVO_API_KEY=...
   ADMIN_EMAIL=...

   # Portmanat (ödəniş, könüllü — boş buraxıla bilər)
   PORTMANAT_MERCHANT_ID=...
   PORTMANAT_SECRET_KEY=...
```

> `PORTMANAT_SECRET_KEY` təyin olunmasa, ödəniş callback-inin imza doğrulaması development rejimində avtomatik keçir (yalnız lokal test üçün uyğundur, production-da mütləq təyin edilməlidir).

4. Admin istifadəçisi verilənlər bazasında (`users` cədvəlində) saxlanılır və `CustomUserDetailsService` vasitəsilə oxunur — ilkin admin hesabını birbaşa bazada yaratmaq lazımdır.

5. Tətbiqi işə sal:
```bash
   ./mvnw spring-boot:run
```

Tətbiq `http://localhost:8080` ünvanında açılacaq.

## 🐳 Docker ilə işə salma

```bash
docker build -t restoran-app .
docker run -p 8080:8080 --env-file .env restoran-app
```

## 🗺️ Gələcək planlar

- [ ] Real Portmanat production sertifikatı ilə tam inteqrasiya
- [ ] Əlaqə/statistika modullarına e-poçt export funksiyası
- [ ] Sosial media (Instagram/Facebook) real linklərin əlavə olunması

## 📄 Lisenziya

Bu layihə [MIT Lisenziyası](LICENSE) altında paylaşılır — sərbəst istifadə, dəyişdirmə və paylaşma hüququ verir, müəllif hüququ bildirişinin qorunması şərti ilə. Ətraflı üçün [LICENSE](LICENSE) faylına bax.

## 👤 Müəllif

**Anar İsmayılov** — Full Stack Developer (Java / Spring Boot)
📧 ismayanar27@gmail.com