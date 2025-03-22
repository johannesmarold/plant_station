# Step 1: Use Java 21 JDK for building
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
# Make mvnw executable
RUN chmod +x mvnw
RUN ./mvnw package -DskipTests

# Step 2: Use Java 21 JRE for running
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "app.jar"]
