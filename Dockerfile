# Stage 1: Build the JAR file
FROM gradle:jdk17-alpine as builder
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# Stage 2: Extract the JAR file
FROM openjdk:17-alpine as extractor
WORKDIR extracted
COPY --from=builder /app/build/libs/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# Stage 3: Create the final image
FROM openjdk:17-alpine
WORKDIR application
COPY --from=extractor extracted/dependencies/ ./
COPY --from=extractor extracted/spring-boot-loader/ ./
COPY --from=extractor extracted/snapshot-dependencies/ ./
COPY --from=extractor extracted/application/ ./

EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
