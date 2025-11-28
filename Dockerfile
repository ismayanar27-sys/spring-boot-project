# 1. Build mərhələsi: Maven və JDK olan rəsmi image istifadə edirik
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Pom faylını kopyalayırıq və dependensiyaları yükləyirik
COPY pom.xml .
# (Bu addım keşləmə üçün yaxşıdır, amma sadəlik üçün birbaşa kopyalayıb yığacağıq)

# Bütün layihəni kopyalayırıq
COPY src ./src

# Layihəni yığırıq (Wrapper istifadə ETMİRİK, birbaşa 'mvn' əmrini işlədirik)
RUN mvn clean package -DskipTests

# 2. Run mərhələsi: Yüngül Java image-i
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Build mərhələsindən yaranan jar faylını kopyalayırıq
COPY --from=build /app/target/*.jar app.jar

# Portu açırıq
EXPOSE 8080

# Tətbiqi işə salırıq
ENTRYPOINT ["java", "-jar", "app.jar"]