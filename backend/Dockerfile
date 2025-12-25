# Build stage
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Install dependencies first for better caching
COPY backend/pom.xml backend/mvnw ./
COPY backend/.mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw -B -DskipTests dependency:go-offline

# Copy source and build
COPY backend/src ./src
RUN ./mvnw -B -DskipTests package

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*SNAPSHOT.jar /app/app.jar
EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
