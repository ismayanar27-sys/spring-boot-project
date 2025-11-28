# 1. Build mərhələsi: Maven və JDK 17
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Pom faylını və mənbə kodunu kopyalayırıq
COPY pom.xml .
COPY src ./src

# Layihəni yığırıq (Testləri atlayaraq)
# Bu əmr bütün asılılıqları yükləyəcək və .jar faylını yaradacaq
RUN mvn clean package -DskipTests

# 2. Run mərhələsi: Yüngül Java image-i
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Yaradılmış .jar faylını kopyalayırıq
COPY --from=build /app/target/*.jar app.jar

# Portu açırıq (Render üçün vacibdir)
EXPOSE 8080

# Tətbiqi işə salırıq
ENTRYPOINT ["java", "-jar", "app.jar"]