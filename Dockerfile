# 1. Build mərhələsi
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Bütün faylları kopyalayırıq
COPY . .

# VACİB DÜZƏLİŞ: Maven üçün yaddaş limiti təyin edirik (Render Free Tier üçün)
ENV MAVEN_OPTS="-Xmx300m"

# Layihəni yığırıq (Testləri atlayırıq)
RUN mvn clean package -DskipTests -B

# 2. Run mərhələsi
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Jar faylını kopyalayırıq
COPY --from=build /app/target/*.jar app.jar

# Port
EXPOSE 8080

# Başlatmaq
ENTRYPOINT ["java", "-jar", "app.jar"]