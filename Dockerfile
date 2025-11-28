# 1. Sizin pom.xml-ə uyğun olaraq Java 17 (JDK) istifadə edirik
FROM eclipse-temurin:17-jdk-alpine

# 2. İşçi qovluğu təyin edirik
WORKDIR /app

# 3. Layihənin bütün fayllarını Docker-ə kopyalayırıq
COPY . .

# 4. Maven wrapper-in işləməsi üçün icazə veririk
RUN chmod +x mvnw

# 5. Layihəni yığırıq (Testləri atlayaraq, sürətli olsun)
RUN ./mvnw clean package -DskipTests

# 6. Tətbiqi işə salırıq
# Sizin pom.xml-də artifactId 'myapp' və version '0.0.1-SNAPSHOT'-dır.
# Ona görə də jar faylı 'myapp-0.0.1-SNAPSHOT.jar' olacaq.
ENTRYPOINT ["java", "-jar", "target/myapp-0.0.1-SNAPSHOT.jar"]