# Étape 1 : build de l'application
FROM eclipse-temurin:17-jdk as builder

WORKDIR /app
COPY . .

# Compile le projet si tu veux builder ici (sinon tu peux ignorer cette étape)
# RUN ./mvnw clean package -DskipTests

# Étape 2 : image finale légère
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copie le jar généré depuis la phase de build
COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
