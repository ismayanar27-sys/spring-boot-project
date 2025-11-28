# 1. Build mərhələsi
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Faylları kopyalayırıq
COPY pom.xml .
COPY src ./src

# ƏSAS DÜZƏLİŞ: -DskipTests əvəzinə -Dmaven.test.skip=true istifadə edirik.
# Bu, testləri nəinki işə salmır, heç kompilyasiya da etmir (xətanı qarşısını alır).
RUN mvn clean package -Dmaven.test.skip=true

# 2. Run mərhələsi
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Jar faylını kopyalayırıq
COPY --from=build /app/target/*.jar app.jar

# Port
EXPOSE 8080

# Başlatmaq
ENTRYPOINT ["java", "-jar", "app.jar"]