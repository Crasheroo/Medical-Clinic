#tworzenie obrazu
FROM eclipse-temurin:21-jdk-alpine

#ystawienie katalogu 'roboczego'
WORKDIR /app

#skompilowanie jarki z target i zmiana nazwy na app.jar
COPY target/Medical-clinic-0.0.1-SNAPSHOT.jar app.jar

# komenda do uruchamiania apki
ENTRYPOINT ["java", "-jar", "app.jar"]